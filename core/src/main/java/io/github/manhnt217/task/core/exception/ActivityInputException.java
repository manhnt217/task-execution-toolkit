package io.github.manhnt217.task.core.exception;

import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.inner.TransformException;

/**
 * @author manhnguyen
 */
public class ActivityInputException extends ActivityException {
    public ActivityInputException(String currentTaskName, String activityName, Exception e) {
        super(currentTaskName, activityName, "Cannot transform the input for activity '" + activityName + "'", e);
    }
}
