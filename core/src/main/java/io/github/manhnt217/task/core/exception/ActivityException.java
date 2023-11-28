package io.github.manhnt217.task.core.exception;

import io.github.manhnt217.task.core.activity.Activity;
import lombok.Getter;

/**
 * @author manh nguyen
 */
@Getter
public class ActivityException extends EngineException {

    private final Activity activity;

    public ActivityException(Activity activity, TaskException e) {
        super("Activity '" + activity.getName() + "' cannot be executed because: An an error occurred in task '" + e.getTaskName() + "'", e);
        this.activity = activity;
    }

    public ActivityException(Activity activity, PluginException e) {
        super("Activity '" + activity.getName() + "' cannot be executed because: An an error occurred in plugin '" + e.getPluginName() + "'", e);
        this.activity = activity;
    }

    public ActivityException(Activity activity, String message, Exception e) {
        super("Activity '" + activity.getName() + "' cannot be executed because: " + message, e);
        this.activity = activity;
    }
}
