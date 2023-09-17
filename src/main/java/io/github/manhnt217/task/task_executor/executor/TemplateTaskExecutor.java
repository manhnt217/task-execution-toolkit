package io.github.manhnt217.task.task_executor.executor;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.process.ExecutionLog;
import io.github.manhnt217.task.task_executor.process.Template;
import io.github.manhnt217.task.task_executor.process.TemplateExecutionException;
import io.github.manhnt217.task.task_executor.task.Task;
import io.github.manhnt217.task.task_executor.task.TemplateTask;

public class TemplateTaskExecutor extends TaskExecutor {
    @Override
    public JsonNode execute(Task task, JsonNode input) {
        if (!(task instanceof TemplateTask)) {
            throw new IllegalArgumentException("Task " + task + " is not a TemplateTask");
        }
        TemplateTask templateTask = (TemplateTask) task;
        try {
            return Template.run(
                    templateTask.getTemplateName(),
                    input,
                    (severity, message) -> this.logs.add(new ExecutionLog(templateTask.getId(), severity, message)));
        } catch (TemplateExecutionException e) {
            throw new TaskExecutionException(e);
        }
    }
}
