package io.github.manhnt217.task.sample.example_plugin;

import io.github.manhnt217.task.core.task.plugin.Plugin;
import io.github.manhnt217.task.core.task.plugin.PluginLogger;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manh nguyen
 */
public class AddTwoNumber extends Plugin<AddTwoNumber.Input, Integer> {

    @Override
    public Class<? extends Input> getInputType() {
        return Input.class;
    }

    @Override
    public Integer exec(Input input, PluginLogger logger) {
        return input.a + input.b;
    }

    @Getter
    @Setter
    public static class Input {
        private int a;
        private int b;
    }
}
