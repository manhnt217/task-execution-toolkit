package io.github.manhnt217.task.core.activity.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.exception.ActivityException;
import lombok.Getter;

/**
 * @author manhnguyen
 */
@Getter
public class PluginActivityException extends ActivityException {

    private final String pluginName;
    private final JsonNode input;

    public PluginActivityException(String taskName, String activityName, String message, String pluginName, JsonNode input) {
        this(taskName, activityName, message, pluginName, input, null);
    }

    public PluginActivityException(String taskName, String activityName, String message, String pluginName, JsonNode input, Exception e) {
        super(taskName, activityName, message, e);
        this.pluginName = pluginName;
        this.input = input;
    }
}
