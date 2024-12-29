package io.github.manhnt217.task.core.activity.future.exception;

import io.github.manhnt217.task.core.activity.Activity;

/**
 * @author manhnguyen
 */
public class WaitCancelledException extends WaitActivityException {
    public WaitCancelledException(Activity activity, String taskName) {
        super(taskName, activity.getName(), "Future task has been cancelled");
    }
}
