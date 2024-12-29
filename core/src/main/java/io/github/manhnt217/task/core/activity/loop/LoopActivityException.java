package io.github.manhnt217.task.core.activity.loop;

import io.github.manhnt217.task.core.exception.ActivityException;

/**
 * @author manhnguyen
 */
public class LoopActivityException extends ActivityException {
    public LoopActivityException(String taskName, String activityName, String message) {
        super(taskName, activityName, message);
    }
}
