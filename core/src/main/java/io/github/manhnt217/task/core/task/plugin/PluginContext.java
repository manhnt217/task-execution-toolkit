package io.github.manhnt217.task.core.task.plugin;

import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.task.TaskContext;
import lombok.Getter;

/**
 * @author manh nguyen
 */
@Getter
public class PluginContext extends TaskContext {
    private final String activityName;

    public PluginContext(String taskName, String activityName, ActivityContext context) {
        super(context);
        setTaskName(taskName);
        this.activityName = activityName;
    }
}
