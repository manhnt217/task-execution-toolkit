package io.github.manhnt217.task.task_executor.process;

public class TemplateLogHandler implements LogHandler {

    private final LogHandler inner;
    private final String taskId;
    private final String executionSessionId;

    public TemplateLogHandler(String executionSessionId, String taskId, LogHandler inner) {
        this.inner = inner;
        this.taskId = taskId;
        this.executionSessionId = executionSessionId;
    }

    public void log(Severity severity, String message) {
        this.inner.log(this.executionSessionId, this.taskId, severity, message);
    }

    @Override
    public void log(String executionSessionId, String taskId, Severity severity, String message) {
        this.inner.log(executionSessionId, taskId, severity, message);
    }
}
