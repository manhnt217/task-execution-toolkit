package io.github.manhnt217.task.task_executor.executor;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.process.Logger;
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
    public JsonNode execute(Task task, JsonNode input, String executionSessionId, Logger logger) throws TaskExecutionException {

        if (!(task instanceof CompoundTask)) {
            throw new IllegalArgumentException("Task '" + task.getTaskName() + "' is not a compound task");
        }
        CompoundTask compoundTask = (CompoundTask) task;

        ParamContext context = new ParamContext();

        context.setParentInput(input);

        try {
            List<Task> executionOrder = resolveDependencies(compoundTask.getSubTasks());
            for (Task subTask : executionOrder) {
                executeTask(subTask, context, executionSessionId, logger);
            }
            return context.transform(compoundTask.getOutputMapping());
        } catch (UnresolvableDependencyException e) {
            throw new TaskExecutionException("Cannot execute task because of unresolvable dependencies", task, e);
        } catch (TaskExecutionException e) {
            throw new SubTaskExecutionException(task, e);
        } catch (Exception e) {
            throw new TaskExecutionException("Unexpected exception occurred", task, e);
        }
    }

    private List<Task> resolveDependencies(List<Task> tasks) throws UnresolvableDependencyException {
        Map<String, Task> taskMap = tasks.stream().collect(Collectors.toMap(Task::getTaskName, Function.identity()));
        List<DependentItem> dependentItems = tasks.stream()
                .map(t -> new DependentItem(t.getTaskName(), new HashSet<>(t.getDependencies())))
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

    private void executeTask(Task task, ParamContext context, String executionSessionId, Logger logger) throws TaskExecutionException {
        JsonNode inputAfterTransform = context.transformInput(task);
        log(task, task.getStartLogExpression(), context, executionSessionId, logger);

        TaskExecutor executor = getTaskExecutor(task);
        JsonNode output = executor.execute(task, inputAfterTransform, executionSessionId, logger);

        context.saveTaskOutput(task, output);
        log(task, task.getEndLogExpression(), context, executionSessionId, logger);
    }

    private void log(Task task, String logExp, ParamContext ctx, String executionSessionId, Logger logger) {
        if (StringUtils.isBlank(logExp)) {
            return;
        }
        try {
            JsonNode jsonNode = ctx.transform(logExp);
            logger.info(executionSessionId, task.getTaskName(), jsonNode.isContainerNode() ? "" : jsonNode.asText());
        } catch (Exception e) {
            logger.warn(executionSessionId, task.getTaskName(), "Error while applying log expression to the context. Expression = " + logExp, e);
        }
    }

}
