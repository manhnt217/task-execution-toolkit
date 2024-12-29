package io.github.manhnt217.task.core.activity.future.exception;

import io.github.manhnt217.task.core.exception.ActivityException;

/**
 * @author manhnguyen
 */
public class WaitActivityException extends ActivityException {
    public WaitActivityException(String taskName, String activityName, String message) {
        super(taskName, activityName, message);
    }

    public WaitActivityException(String taskName, String activityName, String message, Exception e) {
        super(taskName, activityName, message, e);
    }
}
