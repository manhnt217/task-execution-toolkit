package io.github.manhnt217.task.core.activity.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.AbstractActivity;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.exception.ActivityException;
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
        Plugin plugin = getPlugin(pluginName, context);
        Object in1;
        try {
            in1 = JSONUtil.treeToValue(input, plugin.getInputType(), context);
        } catch (Exception e) {
            throw new PluginActivityException(context.getCurrentTaskName(), this.getName(), "Cannot convert input into to plugin's desired type", pluginName, input, e);
        }
        Object rs;
        try {
            rs = plugin.exec(in1, new PluginLoggerImpl(context.getExecutionId(), context.getCurrentTaskName(), this.getName(), context.getLogger()));
        } catch (Exception e) {
            throw new PluginActivityException(context.getCurrentTaskName(), this.getName(), "Exception while executing", pluginName, input, e);
        }
        try {
            JsonNode result = JSONUtil.valueToTree(rs, context);
            return SimpleOutboundMessage.of(result);
        } catch (Exception e) {
            throw new PluginExecutionException(context.getCurrentTaskName(), this.getName(), "Cannot convert plugin's output into JSON", pluginName, input, e);
        }
    }

    private Plugin getPlugin(String pluginName, ActivityContext context) throws PluginActivityException {
        Plugin plugin;
        try {
            plugin = context.getRepo().resolvePlugin(pluginName);
        } catch (Exception e) {
            throw new PluginActivityException(context.getCurrentTaskName(), this.getName(), "Exception while resolving plugin '" + pluginName + "'", null, null);
        }
        if (plugin == null) {
            throw new PluginActivityException(context.getCurrentTaskName(), this.getName(), "Plugin '" + pluginName + "' not found", null, null);
        }
        plugin.setName(pluginName);
        return plugin;
    }
}
