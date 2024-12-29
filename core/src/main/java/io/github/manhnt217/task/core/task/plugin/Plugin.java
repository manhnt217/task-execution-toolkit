package io.github.manhnt217.task.core.task.plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.exception.PluginException;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manh nguyen
 */
public abstract class Plugin<P, R> {

    @Getter @Setter
    private String name;

    public final JsonNode run(JsonNode input, PluginContext context) throws PluginException {
        P in;
        try {
            in = JSONUtil.treeToValue(input, getInputType(), context);
        } catch (JsonProcessingException e) {
            throw new PluginException(name, input, "Cannot convert in to desired type", e);
        }
        R rs;
        try {
            rs = exec(in, new PluginLoggerImpl(context.getExecutionId(), context.getCurrentTaskName(), context.getActivityName(), context.getLogger()));
        } catch (Exception e) {
            throw new PluginException(name, input, "Exception while executing plugin", e);
        }
        return JSONUtil.valueToTree(rs, context);
    }

    protected abstract Class<? extends P> getInputType();

    protected abstract R exec(P input, PluginLogger functionLogger) throws Exception;
}
