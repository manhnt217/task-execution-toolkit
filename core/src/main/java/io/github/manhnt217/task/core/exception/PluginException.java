package io.github.manhnt217.task.core.exception;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author manhnguyen
 */
public class PluginException extends TaskException {
    public PluginException(String taskName, JsonNode input, String message, Throwable e) {
        super(taskName, input, message, e);
    }

    public PluginException(String taskName, String message) {
        super(taskName, message);
    }

    public PluginException(String taskName, JsonNode input, Throwable e) {
        super(taskName, input, e);
    }
}
