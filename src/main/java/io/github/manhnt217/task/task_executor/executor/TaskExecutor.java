package io.github.manhnt217.task.task_executor.executor;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.process.ExecutionLog;
import io.github.manhnt217.task.task_executor.task.Task;
import io.github.manhnt217.task.task_executor.task.TemplateTask;

import java.util.ArrayList;
import java.util.List;

public abstract class TaskExecutor {

    protected final List<ExecutionLog> logs;

    public static TaskExecutor getTaskExecutor(Task task) {
        if (task instanceof TemplateTask) {
            return new TemplateTaskExecutor();
        } else {
            return new CompoundTaskExecutor();
        }
    }

    protected TaskExecutor() {
        this.logs = new ArrayList<>(0);
    }

    public abstract JsonNode execute(Task task, JsonNode input);

    public List<ExecutionLog> getLogs() {
        return this.logs;
    }
}
