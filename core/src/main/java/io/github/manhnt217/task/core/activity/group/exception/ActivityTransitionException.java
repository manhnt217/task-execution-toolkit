package io.github.manhnt217.task.core.activity.group.exception;

/**
 * @author manhnguyen
 */
public class ActivityTransitionException extends ActivityGroupException {
    public ActivityTransitionException(String taskName, String activityName, String message) {
        super(taskName, activityName, message);
    }

    public ActivityTransitionException(String currentTaskName, String activityName, String message, Exception e) {
        super(currentTaskName, activityName, message, e);
    }
}
