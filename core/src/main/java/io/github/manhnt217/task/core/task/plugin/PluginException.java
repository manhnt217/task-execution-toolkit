package io.github.manhnt217.task.core.task.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.exception.ExecutionException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author manh nguyen
 */
@Getter
public class PluginException extends ExecutionException {
    private final String pluginName;
    private final JsonNode input;

    public PluginException(String pluginName, JsonNode input, Throwable e) {
        super(buildMessage(pluginName, input), e);
        this.pluginName = pluginName;
        this.input = input;
    }

    private static String buildMessage(String pluginName, JsonNode input) {
        StringBuilder msg = new StringBuilder("Could not execute plugin '" + pluginName + "'.");
        if (input != null) {
            msg.append(" Input = ").append(input);
        }

        return msg.toString();
    }
}
