package io.github.manhnt217.task.core.task.plugin;

import io.github.manhnt217.task.core.activity.TaskLogger;

/**
 * @author manh nguyen
 */
public class PluginLoggerImpl implements PluginLogger {
    private final String executionId;
    private final String taskName;
    private final String activityName;
    private final TaskLogger logger;

    public PluginLoggerImpl(String executionId, String taskName, String activityName, TaskLogger logger) {

        this.executionId = executionId;
        this.taskName = taskName;
        this.activityName = activityName;
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        this.logger.info(this.executionId, this.taskName, this.activityName, message);
    }

    @Override
    public void warn(String message, Throwable e) {
        this.logger.warn(this.executionId, this.taskName, this.activityName, message, e);

    }

    @Override
    public void error(String message, Throwable e) {
        this.logger.error(this.executionId, this.taskName, this.activityName, message, e);
    }
}
