package io.github.manhnt217.task.core.repo;

import io.github.manhnt217.task.core.task.plugin.Plugin;

/**
 * @author manh nguyen
 */
public interface PluginResolver {

    Plugin<?, ?> resolvePlugin(String pluginName);
}
