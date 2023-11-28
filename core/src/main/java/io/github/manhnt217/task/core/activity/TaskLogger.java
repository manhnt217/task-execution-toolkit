package io.github.manhnt217.task.core.activity;

/**
 * @author manh nguyen
 */
public interface TaskLogger {

    void info(String executionId, String taskName, String activityName, String message);

    void warn(String executionId, String taskName, String activityName, String message, Throwable e);

    void error(String executionId, String taskName, String activityName, String message, Throwable e);
}
