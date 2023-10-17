package io.github.manhnt217.task.task_executor.activity;

import io.github.manhnt217.task.task_executor.task.TaskExecutionException;
import lombok.Getter;

/**
 * @author manhnguyen
 */
@Getter
public class ActivityExecutionException extends ActivityException {

    private final Activity activity;

    public ActivityExecutionException(Activity activity, TaskExecutionException e) {
        super("Activity '" + activity.getName() + "' cannot be executed because: An an error occurred in task '" + e.getTaskName() + "'", e);
        this.activity = activity;
    }


    public ActivityExecutionException(String message, Activity activity, Exception e) {
        super("Activity '" + activity.getName() + "' cannot be executed because: " + message, e);
        this.activity = activity;
    }
}
