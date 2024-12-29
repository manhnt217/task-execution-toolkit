package io.github.manhnt217.task.core.activity.plugin;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author manhnguyen
 */
public class PluginExecutionException extends PluginActivityException {
    public PluginExecutionException(String taskName, String activityName, String message, String pluginName, JsonNode input, Exception e) {
        super(taskName, activityName, message, pluginName, input, e);
    }
}
