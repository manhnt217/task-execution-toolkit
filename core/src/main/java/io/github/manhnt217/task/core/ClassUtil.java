package io.github.manhnt217.task.core;

/**
 * @author manhnguyen
 */
public class ClassUtil {
    public static <T> Class<T> findPlugin(String pluginClassName, Class<T> pluginClass) {
        try {
            Class<?> clazz = Class.forName(pluginClassName);
            if (!pluginClass.isAssignableFrom(clazz)) {
                throw new RuntimeException("Given class does not extend " + pluginClass.getName());
            }
            return (Class<T>) clazz;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find any plugin with name '" + pluginClassName + "'");
        }
    }

    public static <P> P newPluginInstance(Class<? extends P> pluginClass) {
        try {
            return pluginClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Exception while instantiate plugin class '" + pluginClass.getName() + "'");
        }
    }
}
