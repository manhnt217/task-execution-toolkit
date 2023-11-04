package io.github.manhnt217.task.task_engine.task;

import io.github.manhnt217.task.task_engine.activity.ActivityLogger;

/**
 * @author manhnguyen
 */
public class TaskLogger {

    private final ActivityLogger inner;
    private final String activityName;
    private final String executionId;

    public TaskLogger(String executionId, String activityName, ActivityLogger inner) {
        this.inner = inner;
        this.activityName = activityName;
        this.executionId = executionId;
    }

    public void info(String message) {
        this.inner.info(this.executionId, this.activityName, message);
    }

    public void warn(String message, Throwable e) {
        this.inner.warn(this.executionId, this.activityName, message, e);
    }

    public void error(String message, Throwable e) {
        this.inner.error(this.executionId, this.activityName, message, e);
    }
}
