package io.github.manhnt217.task.task_executor.executor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import io.github.manhnt217.task.task_executor.process.ExecutionLog;
import io.github.manhnt217.task.task_executor.process.Severity;
import io.github.manhnt217.task.task_executor.task.CompoundTask;
import io.github.manhnt217.task.task_executor.task.Task;
import io.github.manhnt217.task.task_executor.task.TaskExecutionContext;
import io.github.manhnt217.task.task_executor.task.TaskExecutionException;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.manhnt217.task.task_executor.Main.om;

public class CompoundTaskExecutor extends TaskExecutor {

    @Override
    public JsonNode execute(Task task, JsonNode input) {

        if (!(task instanceof CompoundTask)) {
            throw new IllegalArgumentException("Task " + task + " is not a compound task");
        }
        CompoundTask compoundTask = (CompoundTask) task;

        TaskExecutionContext context = new TaskExecutionContext();

        context.saveInitialInput(input);

        List<Task> executionOrder = resolveDependencies(compoundTask.getSubTasks());
        for (Task subTask : executionOrder) {
            executeTask(subTask, context);
        }

        return context.applyTransform(task.getOutputMappingExpression());
    }

    private List<Task> resolveDependencies(Set<Task> subTasks) {
        // TODO: dummy implementation
        return subTasks.stream().sorted(Comparator.comparing(Task::getId)).collect(Collectors.toList());
    }

    // TODO: Deal with TaskExecutionException
    private void executeTask(Task task, TaskExecutionContext context) {

        JsonNode inputAfterTransform = extractInput(task, context);

        context.saveInput(task, inputAfterTransform);
        log(task.getStartLogExpression(), context);

        TaskExecutor executor = getTaskExecutor(task);
        JsonNode output = executor.execute(task, inputAfterTransform);
        logs.addAll(executor.getLogs());

        context.saveOutput(task, output);
        log(task.getEndLogExpression(), context);
    }

    private static JsonNode extractInput(Task task, TaskExecutionContext context) {
        if (task.getInputType() == null || task.getInputType() == Task.InputType.NONE) {
            return NullNode.getInstance();
        } else if (Task.InputType.CONTEXT.equals(task.getInputType())) {
            return context.applyTransform(task.getInputMappingExpression());
        } else if (Task.InputType.PREVIOUS_TASK.equals(task.getInputType()) && task.getDependencies().size() == 1) {
            return context.applyFromTaskOutput(task.getDependencies().get(0), task.getInputMappingExpression());
        } else {
            throw new TaskExecutionException("A task should depend on ONLY ONE task when its InputType = Previous Task");
        }
    }

    private void log(String jslt, TaskExecutionContext ctx) {
        if (StringUtils.isBlank(jslt)) {
            return;
        }
        try {
            JsonNode jsonNode = ctx.applyTransform(jslt);
            logs.add(new ExecutionLog(Severity.INFO, jsonNode.isContainerNode() ? om.writeValueAsString(jsonNode) : jsonNode.asText()));
        } catch (Exception e) {
            logs.add(new ExecutionLog(Severity.WARN, "Error while applying log expression to the context. Expression = " + jslt));
        }
    }

}
