package io.github.manhnt217.task.core.exception;

import io.github.manhnt217.task.core.task.TaskException;
import io.github.manhnt217.task.core.task.plugin.PluginException;
import lombok.Getter;

/**
 * @author manh nguyen
 */
@Getter
public class ActivityException extends ExecutionException {

    private final String activityName;
    private final String taskName;

    public ActivityException(TaskException e, String activityName) {
        super("Activity '" + activityName + "' cannot be executed because: An exception was thrown in function '" + e.getTaskName() + "'", e);
        this.activityName = activityName;
        this.taskName = e.getTaskName();
    }

    public ActivityException(String taskName, String activityName, PluginException e) {
        super("Activity '" + activityName + "' cannot be executed because: An an error occurred in plugin '" + e.getPluginName() + "'", e);
        this.activityName = activityName;
        this.taskName = taskName;
    }

    public ActivityException(String taskName, String activityName, String message, Exception e) {
        super("Activity '" + activityName + "' cannot be executed because: " + message, e);
        this.activityName = activityName;
        this.taskName = taskName;
    }

    public ActivityException(String taskName, String activityName, String message) {
        super("Activity '" + activityName + "' cannot be executed because: " + message);
        this.activityName = activityName;
        this.taskName = taskName;
    }
}
