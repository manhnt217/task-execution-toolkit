package io.github.manhnt217.task.persistence.builder;

/**
 * @author manh nguyen
 */
public class ActivityBuilder {

    public static ForEachActivityBuilder forEach() {
        return new ForEachActivityBuilder();
    }

    public static GroupActivityBuilder group(boolean synced) {
        return new GroupActivityBuilder(synced);
    }

    public static FunctionCallActivityBuilder funcCall(String name) {
        return new FunctionCallActivityBuilder(name);
    }

    public static PluginBuilder plugin(String name, String pluginName) {
        return new PluginBuilder(name, pluginName);
    }

    public static FromSourceBuilder fromSource(String name, String sourceName) {
        return new FromSourceBuilder(name, sourceName);
    }

    public static FunctionBuilder function(String name) {
        return new FunctionBuilder(name);
    }

    public static HandlerBuilder handler(String name) {
        return new HandlerBuilder(name);
    }
}
