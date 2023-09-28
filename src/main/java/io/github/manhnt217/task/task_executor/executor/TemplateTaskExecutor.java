package io.github.manhnt217.task.task_executor.executor;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.process.Logger;
import io.github.manhnt217.task.task_executor.process.Template;
import io.github.manhnt217.task.task_executor.process.TemplateExecutionException;
import io.github.manhnt217.task.task_executor.process.TemplateLogger;
import io.github.manhnt217.task.task_executor.task.Task;
import io.github.manhnt217.task.task_executor.task.TemplateTask;

public class TemplateTaskExecutor extends TaskExecutor {

    @Override
    public JsonNode execute(Task task, JsonNode input, String executionSessionId, Logger logger) throws TaskExecutionException {
        if (!(task instanceof TemplateTask)) {
            throw new IllegalArgumentException("Task " + task + " is not a TemplateTask");
        }
        TemplateTask templateTask = (TemplateTask) task;
        try {
            return Template.run(
                    templateTask.getTemplateName(),
                    input,
                    new TemplateLogger(executionSessionId, task.getId(), logger));
        } catch (TemplateExecutionException e) {
            throw new TaskExecutionException(task, e);
        } catch (Exception e) {
            throw new TaskExecutionException("An exception was thrown during the task execution", task, e);
        }
    }
}
