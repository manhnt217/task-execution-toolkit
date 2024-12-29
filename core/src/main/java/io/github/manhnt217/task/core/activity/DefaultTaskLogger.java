package io.github.manhnt217.task.core.activity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author manh nguyen
 */
@Getter
public class DefaultTaskLogger implements TaskLogger {

    private final List<ExecutionLog> logs = new ArrayList<>();

    @Override
    public void info(String executionId, String taskName, String activityName, String message) {
        logs.add(new ExecutionLog(executionId, taskName, activityName, ExecutionLog.Severity.INFO, message, null));
    }

    @Override
    public void warn(String executionId, String taskName, String activityName, String message, Throwable e) {
        logs.add(new ExecutionLog(executionId, taskName, activityName, ExecutionLog.Severity.WARN, message, e));
    }

    @Override
    public void error(String executionId, String taskName, String activityName, String message, Throwable e) {
        logs.add(new ExecutionLog(executionId, taskName, activityName, ExecutionLog.Severity.ERROR, message, e));
    }
}
