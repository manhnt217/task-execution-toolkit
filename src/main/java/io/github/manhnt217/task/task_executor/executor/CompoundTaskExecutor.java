package io.github.manhnt217.task.task_executor.executor;

import com.fasterxml.jackson.databind.JsonNode;
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

        context.setParentInput(input);

        List<Task> executionOrder = null;
        try {
            executionOrder = resolveDependencies(compoundTask.getSubTasks());
        } catch (UnresolvableDependencyException e) {
            throw new TaskExecutionException("Cannot execute task because of unresolvable dependencies", e);
        }
        for (Task subTask : executionOrder) {
            executeTask(subTask, context);
        }

        return context.allTaskOutputs();
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
        log(task, task.getStartLogExpression(), context);

        TaskExecutor executor = getTaskExecutor(task);
        JsonNode output = executor.execute(task, inputAfterTransform);
        logs.addAll(executor.getLogs());

        context.saveTaskOutput(task, output);
        log(task, task.getEndLogExpression(), context);
    }

    private static JsonNode extractInput(Task task, ParamContext context) {
        return context.transformInput(task);
    }

    private void log(Task task, String jslt, ParamContext ctx) {
        if (StringUtils.isBlank(jslt)) {
            return;
        }
        try {
            JsonNode jsonNode = ctx.transform(jslt);
            logs.add(new ExecutionLog(task.getId(), Severity.INFO, jsonNode.isContainerNode() ? "" : jsonNode.asText()));
        } catch (Exception e) {
            logs.add(new ExecutionLog(task.getId(), Severity.WARN, "Error while applying log expression to the context. Expression = " + jslt));
        }
    }

}
