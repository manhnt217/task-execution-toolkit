package io.github.manhnt217.task.core.activity.trial;

import io.github.manhnt217.task.core.exception.ActivityException;

public class CustomActivityException extends ActivityException {
    public CustomActivityException(String currentTaskName, String activityName, String message, Exception e) {
        super(currentTaskName, activityName, message, e);
    }
}
