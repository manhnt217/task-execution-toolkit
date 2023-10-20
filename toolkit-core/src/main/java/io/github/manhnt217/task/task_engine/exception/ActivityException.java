package io.github.manhnt217.task.task_engine.exception;

import io.github.manhnt217.task.task_engine.activity.Activity;
import lombok.Getter;

/**
 * @author manhnguyen
 */
@Getter
public class ActivityException extends EngineException {

    private final Activity activity;

    public ActivityException(Activity activity, TaskException e) {
        super("Activity '" + activity.getName() + "' cannot be executed because: An an error occurred in task '" + e.getTaskName() + "'", e);
        this.activity = activity;
    }


    public ActivityException(Activity activity, String message, Exception e) {
        super("Activity '" + activity.getName() + "' cannot be executed because: " + message, e);
        this.activity = activity;
    }
}
