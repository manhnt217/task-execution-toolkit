package io.github.manhnt217.task.core.exception;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author manh nguyen
 */
@Getter
public class PluginException extends EngineException {
    private final String pluginName;
    private final JsonNode input;

    public PluginException(String pluginName, JsonNode input, String message, Throwable e) {
        super(buildMessage(pluginName, input, message), e);
        this.pluginName = pluginName;
        this.input = input;
    }

    public PluginException(String pluginName, String message) {
        this(pluginName, null, message, null);
    }

    private static String buildMessage(String pluginName, JsonNode input, String message) {
        StringBuilder msg = new StringBuilder("Could not execute plugin '" + pluginName + "'.");
        if (StringUtils.isNotBlank(message)) {
            msg.append(" Because ").append(message).append(".");
        }
        if (input != null) {
            msg.append(" Input = ").append(input);
        }

        return msg.toString();
    }
}
