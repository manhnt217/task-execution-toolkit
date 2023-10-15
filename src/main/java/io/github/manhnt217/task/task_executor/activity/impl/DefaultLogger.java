package io.github.manhnt217.task.task_executor.activity.impl;

import io.github.manhnt217.task.task_executor.activity.Logger;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DefaultLogger implements Logger {

    private final List<ExecutionLog> logs = new ArrayList<>();

    @Override
    public void info(String executionSessionId, String taskId, String message) {
        logs.add(new ExecutionLog(executionSessionId, taskId, ExecutionLog.Severity.INFO, message, null));
    }

    @Override
    public void warn(String executionSessionId, String taskId, String message, Throwable e) {
        logs.add(new ExecutionLog(executionSessionId, taskId, ExecutionLog.Severity.WARN, message, e));
    }

    @Override
    public void error(String executionSessionId, String taskId, String message, Throwable e) {
        logs.add(new ExecutionLog(executionSessionId, taskId, ExecutionLog.Severity.ERROR, message, e));
    }
}
