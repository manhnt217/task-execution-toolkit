package io.github.manhnt217.task.core.activity.future.exception;

import io.github.manhnt217.task.core.activity.Activity;

/**
 * @author manhnguyen
 */
public class WaitRuntimeException extends WaitActivityException {
    public WaitRuntimeException(Activity activity, String taskName, Exception e) {
        super(taskName, activity.getName(), "Future task thrown an exception", e);
    }
}
