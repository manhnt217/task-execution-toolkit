package io.github.manhnt217.task.core.task.plugin;

import lombok.Getter;
import lombok.Setter;

/**
 * @author manh nguyen
 */
public abstract class Plugin<P, R> {

    @Getter @Setter
    private String name;

    public abstract Class<? extends P> getInputType();

    public abstract R exec(P input, PluginLogger functionLogger) throws Exception;
}
