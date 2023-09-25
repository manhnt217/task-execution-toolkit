package io.github.manhnt217.task.task_executor.executor;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.process.LogHandler;
import io.github.manhnt217.task.task_executor.process.Severity;
import io.github.manhnt217.task.task_executor.task.CompoundTask;
import io.github.manhnt217.task.task_executor.task.Task;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompoundTaskExecutor extends TaskExecutor {

    @Override
    public JsonNode execute(Task task, JsonNode input, String executionSessionId, LogHandler logHandler) throws TaskExecutionException {

        if (!(task instanceof CompoundTask)) {
            throw new IllegalArgumentException("Task " + task + " is not a compound task");
        }
        CompoundTask compoundTask = (CompoundTask) task;

        ParamContext context = new ParamContext();

        context.setParentInput(input);

        try {
            List<Task> executionOrder = resolveDependencies(compoundTask.getSubTasks());
            for (Task subTask : executionOrder) {
                executeTask(subTask, context, executionSessionId, logHandler);
            }
            return context.allTaskOutputs();
        } catch (UnresolvableDependencyException e) {
            throw new TaskExecutionException("Cannot execute task because of unresolvable dependencies. " + e.getMessage(), e, task);
        } catch (TaskExecutionException e) {
            throw new SubTaskExecutionException(task, e);
        }
    }

    private List<Task> resolveDependencies(List<Task> tasks) throws UnresolvableDependencyException {
        Map<String, Task> taskMap = tasks.stream().collect(Collectors.toMap(Task::getId, Function.identity()));
        List<DependentItem> dependentItems = tasks.stream()
                .map(t -> new DependentItem(t.getId(), new HashSet<>(t.getDependencies())))
                        .collect(Collectors.toList());
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

    private void executeTask(Task task, ParamContext context, String executionSessionId, LogHandler logHandler) throws TaskExecutionException {
        JsonNode inputAfterTransform = extractInput(task, context);
        log(task, task.getStartLogExpression(), context, executionSessionId, logHandler);

        TaskExecutor executor = getTaskExecutor(task);
        JsonNode output = executor.execute(task, inputAfterTransform, executionSessionId, logHandler);

        context.saveTaskOutput(task, output);
        log(task, task.getEndLogExpression(), context, executionSessionId, logHandler);
    }

    private static JsonNode extractInput(Task task, ParamContext context) throws TaskExecutionException {
        return context.transformInput(task);
    }

    private void log(Task task, String jslt, ParamContext ctx, String executionSessionId, LogHandler logHandler) {
        if (StringUtils.isBlank(jslt)) {
            return;
        }
        try {
            JsonNode jsonNode = ctx.transform(jslt);
            logHandler.log(executionSessionId, task.getId(), Severity.INFO, jsonNode.isContainerNode() ? "" : jsonNode.asText());
        } catch (Exception e) {
            logHandler.log(executionSessionId, task.getId(), Severity.WARN, "Error while applying log expression to the context. Expression = " + jslt);
        }
    }

}
