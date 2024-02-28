package io.github.manhnt217.task.core.activity.simple;

import io.github.manhnt217.task.core.exception.ActivityException;

public class JsonParserActivityException extends ActivityException {
    public JsonParserActivityException(String currentTaskName, String activityName, String message, Exception e) {
        super(currentTaskName, activityName, message, e);
    }

    public JsonParserActivityException(String currentTaskName, String activityName, String message) {
        super(currentTaskName, activityName, message);
    }
}
