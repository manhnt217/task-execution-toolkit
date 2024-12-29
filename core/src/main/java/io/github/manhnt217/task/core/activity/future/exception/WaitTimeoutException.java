package io.github.manhnt217.task.core.activity.future.exception;

import io.github.manhnt217.task.core.activity.Activity;

/**
 * @author manhnguyen
 */
public class WaitTimeoutException extends WaitActivityException {
    public WaitTimeoutException(Activity activity, String taskName) {
        super(taskName, activity.getName(), "Wait timed out");
    }
}
