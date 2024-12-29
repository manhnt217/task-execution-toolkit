package io.github.manhnt217.task.core.activity.func;

import io.github.manhnt217.task.core.exception.ActivityException;

/**
 * @author manhnguyen
 */
public class FunctionCallActivityException extends ActivityException {

    public FunctionCallActivityException(String taskName, String activityName, String message, Exception e) {
        super(taskName, activityName, message, e);
    }
}
