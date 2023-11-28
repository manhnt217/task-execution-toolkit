package io.github.manhnt217.task.sample.plugin;

import io.github.manhnt217.task.core.task.plugin.Plugin;
import io.github.manhnt217.task.core.task.plugin.PluginLogger;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manh nguyen
 */
public class AddTwoNumber extends Plugin<AddTwoNumber.Input, Integer> {

    @Override
    protected Class<? extends Input> getInputType() {
        return Input.class;
    }

    @Override
    public Integer exec(AddTwoNumber.Input input, PluginLogger logger) {
        return input.a + input.b;
    }

    @Getter
    @Setter
    public static class Input {
        private int a;
        private int b;
    }
}
