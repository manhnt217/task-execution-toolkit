package io.github.manhnt217.task.task_executor.process;

public class TemplateLogger {

    private final Logger inner;
    private final String taskId;
    private final String executionSessionId;

    public TemplateLogger(String executionSessionId, String taskId, Logger inner) {
        this.inner = inner;
        this.taskId = taskId;
        this.executionSessionId = executionSessionId;
    }

    public void info(String message) {
        this.inner.info(this.executionSessionId, this.taskId, message);
    }

    public void warn(String message, Throwable e) {
        this.inner.warn(this.executionSessionId, this.taskId, message, e);
    }

    public void error(String message, Throwable e) {
        this.inner.error(this.executionSessionId, this.taskId, message, e);
    }
}
