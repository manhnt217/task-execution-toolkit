package io.github.manhnt217.task.task_executor.executor;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.process.*;
import io.github.manhnt217.task.task_executor.task.Task;
import io.github.manhnt217.task.task_executor.task.TemplateTask;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class TemplateTaskExecutor extends TaskExecutor {

    @Override
    public JsonNode execute(Task task, JsonNode input, String executionSessionId, LogHandler logHandler) throws TaskExecutionException {
        if (!(task instanceof TemplateTask)) {
            throw new IllegalArgumentException("Task " + task + " is not a TemplateTask");
        }
        TemplateTask templateTask = (TemplateTask) task;
        try {
            return Template.run(
                    templateTask.getTemplateName(),
                    input,
                    new TemplateLogHandler(executionSessionId, task.getId(), logHandler));
        } catch (TemplateExecutionException e) {
            throw new TaskExecutionException(e, task);
        } catch (Exception e) {
            throw new TaskExecutionException("An exception was thrown during the task execution. Stacktrace: " + ExceptionUtils.getRootCauseMessage(e), task);
        }
    }
}
