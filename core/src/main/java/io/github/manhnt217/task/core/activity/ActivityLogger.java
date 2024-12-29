package io.github.manhnt217.task.core.activity;

/**
 * @author manhnguyen
 */
public interface ActivityLogger {
    void info(String executionSessionId, String activityName, String message);

    void warn(String executionSessionId, String activityName, String message, Throwable e);

    void error(String executionSessionId, String activityName, String message, Throwable e);
}
