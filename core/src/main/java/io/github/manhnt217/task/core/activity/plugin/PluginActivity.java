package io.github.manhnt217.task.core.activity.plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.AbstractActivity;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.PluginException;
import io.github.manhnt217.task.core.task.plugin.Plugin;
import io.github.manhnt217.task.core.task.plugin.PluginLoggerImpl;

/**
 * @author manh nguyen
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class PluginActivity extends AbstractActivity {

    private final String pluginName;

    public PluginActivity(String name, String pluginName) {
        super(name);
        this.pluginName = pluginName;
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityContext context) throws ActivityException {
        JsonNode input = in.getContent();
        try {
            return executePlugin(context, input);
        } catch (PluginException e) {
            throw new ActivityException(this, e);
        } catch (Exception e) {
            throw new ActivityException(this, new PluginException(pluginName, input, null, e));
        }
    }

    private SimpleOutboundMessage executePlugin(ActivityContext context, JsonNode input) throws PluginException {
        Plugin plugin = getPlugin(pluginName, context);
        Object in;
        try {
            in = JSONUtil.treeToValue(input, plugin.getInputType(), context);
        } catch (JsonProcessingException e) {
            throw new PluginException(pluginName, input, "Cannot convert in to desired type", e);
        }
        Object rs;
        try {
            rs = plugin.exec(in, new PluginLoggerImpl(context.getExecutionId(), context.getCurrentTaskName(), this.getName(), context.getLogger()));
        } catch (Exception e) {
            throw new PluginException(pluginName, input, "Exception while executing plugin", e);
        }
        JsonNode result = JSONUtil.valueToTree(rs, context);
        return SimpleOutboundMessage.of(result);
    }

    private static Plugin getPlugin(String pluginName, ActivityContext context) throws PluginException {
        Plugin plugin = context.getRepo().resolvePlugin(pluginName);
        if (plugin == null) {
            throw new PluginException(pluginName, "Plugin not found");
        }
        plugin.setName(pluginName);
        return plugin;
    }
}
