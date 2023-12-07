package io.github.manhnt217.task.persistence.builder;

/**
 * @author manh nguyen
 */
public class ActivityBuilder {

    public static ForEachActivityBuilder forEach() {
        return new ForEachActivityBuilder();
    }

    public static GroupActivityBuilder group(String name, boolean synced) {
        return new GroupActivityBuilder(name, synced);
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

    public static  <P, R> FunctionBuilder<P, R> function(String name, Class<? extends P> inputClass, Class<? extends R> outputClass) {
        return new FunctionBuilder<>(name, inputClass, outputClass);
    }

    public static <P> FunctionBuilder<P, Void> consumer(String name, Class<? extends P> inputClass) {
        return new FunctionBuilder<>(name, inputClass, null);
    }

    public static <R> FunctionBuilder<Void, R> producer(String name, Class<? extends R> outputClass) {
        return new FunctionBuilder<>(name, null, outputClass);
    }

    public static FunctionBuilder<Void, Void> routine(String name) {
        return new FunctionBuilder<>(name, null, null);
    }

    public static HandlerBuilder handler(String name) {
        return new HandlerBuilder(name);
    }
}
