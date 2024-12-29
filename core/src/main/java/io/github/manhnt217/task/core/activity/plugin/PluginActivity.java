package io.github.manhnt217.task.core.activity.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.ClassUtil;
import io.github.manhnt217.task.core.activity.AbstractActivity;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.task.plugin.PluginContext;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.PluginException;
import io.github.manhnt217.task.core.task.plugin.Plugin;

/**
 * @author manh nguyen
 */
public class PluginActivity extends AbstractActivity {

    private final String pluginName;

    public PluginActivity(String name, String pluginName) {
        super(name);
        this.pluginName = pluginName;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public OutboundMessage process(InboundMessage in, ActivityContext context) throws ActivityException {
        JsonNode input = in.getContent();
        try {
            Plugin plugin = createPluginInstance(context);
            plugin.setName(pluginName);
            JsonNode result = plugin.run(input, new PluginContext(context.getCurrentTaskName(), this.getName(), context));
            return SimpleOutboundMessage.of(result);
        } catch (PluginException e) {
            throw new ActivityException(this, e);
        } catch (Exception e) {
            throw new ActivityException(this, new PluginException(pluginName, input, null, e));
        }
    }

    private Plugin createPluginInstance(ActivityContext context) throws PluginException {
        Class<? extends Plugin> clazz = context.getRepo().resolvePluginClass(pluginName);
        if (clazz == null) {
            throw new PluginException(pluginName, "Plugin not found");
        }

        Plugin plugin = ClassUtil.newPluginInstance(clazz);
        return plugin;
    }
}
