package io.github.manhnt217.task.task_engine.activity;

/**
 * @author manhnguyen
 */
public interface ActivityLogger {
    void info(String executionSessionId, String activityName, String message);

    void warn(String executionSessionId, String activityName, String message, Throwable e);

    void error(String executionSessionId, String activityName, String message, Throwable e);
}
