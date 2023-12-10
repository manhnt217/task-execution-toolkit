package io.github.manhnt217.task.core.exception;

import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.inner.ContextException;

/**
 * @author manhnguyen
 */
public class ActivityOutputException extends ActivityException {
    public ActivityOutputException(String currentTaskName, String activityName, Exception e) {
        super(currentTaskName, activityName, "Error while saving output for activity '" + activityName + "'", e);
    }
}
