package io.github.manhnt217.task.sample.plugin;

import io.github.manhnt217.task.core.task.PluginTask;
import io.github.manhnt217.task.core.task.TaskLogger;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
public class AddTwoNumberTask extends PluginTask<AddTwoNumberTask.Input, Integer> {
    public AddTwoNumberTask(String name) {
        super(name);
    }

    @Override
    protected Class<? extends AddTwoNumberTask.Input> getInputClass() {
        return Input.class;
    }

    @Override
    public Integer exec(AddTwoNumberTask.Input input, TaskLogger taskLogger) {
        return input.a + input.b;
    }

    @Getter
    @Setter
    public static class Input {
        private int a;
        private int b;
    }
}
