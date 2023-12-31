package io.github.manhnt217.task.sample.example_plugin;

import io.github.manhnt217.task.core.task.plugin.Plugin;
import io.github.manhnt217.task.core.task.plugin.PluginLogger;
import io.github.manhnt217.task.core.type.ObjectRef;

/**
 * @author manh nguyen
 */
public class ObjectRefProducer extends Plugin<Object, ObjectRef> {

    @Override
    public Class<?> getInputType() {
        return Object.class;
    }

    @Override
    public ObjectRef exec(Object input, PluginLogger logger) throws Exception {
        FortyTwo fortyTwo = new FortyTwo();
        fortyTwo.init();
        return new ObjectRef(fortyTwo);
    }

    public static class FortyTwo {
        private int value = 0;

        public void init() {
            value = 42;
        }

        public int getNumberOfTheUniverse() {
            return value;
        }
    }
}
