package io.github.manhnt217.task.core.activity.group.exception;

import io.github.manhnt217.task.core.exception.ActivityException;

/**
 * @author manhnguyen
 */
public class ActivityGroupException extends ActivityException {
    public ActivityGroupException(String taskName, String activityName, String message) {
        super(taskName, activityName, message);
    }

    public ActivityGroupException(String currentTaskName, String activityName, String message, Exception e) {
        super(currentTaskName, activityName, message, e);
    }
}
