package io.github.manhnt217.task.task_executor.executor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import io.github.manhnt217.task.task_executor.process.ExecutionLog;
import io.github.manhnt217.task.task_executor.process.Severity;
import io.github.manhnt217.task.task_executor.task.CompoundTask;
import io.github.manhnt217.task.task_executor.task.Task;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompoundTaskExecutor extends TaskExecutor {

    @Override
    public JsonNode execute(Task task, JsonNode input) {

        if (!(task instanceof CompoundTask)) {
            throw new IllegalArgumentException("Task " + task + " is not a compound task");
        }
        CompoundTask compoundTask = (CompoundTask) task;

        ParamContext context = new ParamContext();

        context.saveGlobalInput(input);

        List<Task> executionOrder = null;
        try {
            executionOrder = resolveDependencies(compoundTask.getSubTasks());
        } catch (UnresolvableDependencyException e) {
            throw new TaskExecutionException("Cannot execute task because of unresolvable dependencies", e);
        }
        for (Task subTask : executionOrder) {
            executeTask(subTask, context);
        }

        return context.applyTransform(task.getOutputMappingExpression());
    }

    private List<Task> resolveDependencies(Set<Task> tasks) throws UnresolvableDependencyException {
        Map<String, Task> taskMap = tasks.stream().collect(Collectors.toMap(Task::getId, Function.identity()));
        Set<DependentItem> dependentItems = tasks.stream()
                .map(t -> new DependentItem(t.getId(), new HashSet<>(t.getDependencies())))
                        .collect(Collectors.toSet());
        DependencyResolver resolver = new DependencyResolver(dependentItems);
        List<Task> result = new ArrayList<>();

        for (DependentItem nextItem; (nextItem = resolver.next()) != null; ) {
            if (nextItem == null) {
                break;
            } else {
                result.add(taskMap.get(nextItem.getName()));
            }
        }

        return result;
    }

    // TODO: Deal with TaskExecutionException
    private void executeTask(Task task, ParamContext context) {

        JsonNode inputAfterTransform = extractInput(task, context);

        context.saveTaskInput(task, inputAfterTransform);
        log(task, task.getStartLogExpression(), context);

        TaskExecutor executor = getTaskExecutor(task);
        JsonNode output = executor.execute(task, inputAfterTransform);
        logs.addAll(executor.getLogs());

        context.saveTaskOutput(task, output);
        log(task, task.getEndLogExpression(), context);
    }

    private static JsonNode extractInput(Task task, ParamContext context) {
        if (task.getInputType() == null || task.getInputType() == Task.InputType.NONE) {
            return NullNode.getInstance();
        } else if (Task.InputType.CONTEXT.equals(task.getInputType())) {
            return context.applyTransform(task.getInputMappingExpression());
        } else if (Task.InputType.PREVIOUS_TASK.equals(task.getInputType()) && task.getDependencies().size() == 1) {
            return context.applyFromTaskOutput(task.getDependencies().iterator().next(), task.getInputMappingExpression());
        } else {
            throw new TaskExecutionException("A task should depend on ONLY ONE task when its InputType = Previous Task");
        }
    }

    private void log(Task task, String jslt, ParamContext ctx) {
        if (StringUtils.isBlank(jslt)) {
            return;
        }
        try {
            JsonNode jsonNode = ctx.applyTransform(jslt);
            logs.add(new ExecutionLog(task.getId(), Severity.INFO, jsonNode.isContainerNode() ? om.writeValueAsString(jsonNode) : jsonNode.asText()));
        } catch (Exception e) {
            logs.add(new ExecutionLog(task.getId(), Severity.WARN, "Error while applying log expression to the context. Expression = " + jslt));
        }
    }

}
