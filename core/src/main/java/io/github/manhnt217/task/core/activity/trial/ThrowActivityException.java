package io.github.manhnt217.task.core.activity.trial;

import io.github.manhnt217.task.core.exception.ActivityException;

/**
 * @author manhnguyen
 */
public class ThrowActivityException extends ActivityException {
    public ThrowActivityException(String taskName, String activityName, String message) {
        super(taskName, activityName, message);
    }

    public ThrowActivityException(String taskName, String activityName, String message, Exception e) {
        super(taskName, activityName, message, e);
    }
}
