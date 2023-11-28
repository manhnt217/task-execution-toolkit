package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.activity.plugin.PluginActivity;

/**
 * @author manh nguyen
 */
public class PluginBuilder extends AbstractActivityBuilder<PluginActivity, PluginBuilder> {

    private final String name;
    private final String pluginName;

    PluginBuilder(String name, String pluginName) {
        this.name = name;
        this.pluginName = pluginName;
    }

    @Override
    public PluginActivity build() {
        PluginActivity pluginActivity = new PluginActivity(name, pluginName);
        pluginActivity.setInputMapping(inputMapping);
        return pluginActivity;
    }
}
