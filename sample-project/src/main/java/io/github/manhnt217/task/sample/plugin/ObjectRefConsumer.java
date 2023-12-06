package io.github.manhnt217.task.sample.plugin;

import io.github.manhnt217.task.core.context.ObjectRef;
import io.github.manhnt217.task.core.task.plugin.Plugin;
import io.github.manhnt217.task.core.task.plugin.PluginLogger;

/**
 * @author manh nguyen
 */
public class ObjectRefConsumer extends Plugin<ObjectRef, Object> {

    @Override
    public Class<? extends ObjectRef> getInputType() {
        return ObjectRef.class;
    }

    @Override
    public Object exec(ObjectRef input, PluginLogger logger) throws Exception {
        ObjectRefProducer.FortyTwo fortyTwo = input.get();
        int numberOfTheUniverse = fortyTwo.getNumberOfTheUniverse();
        if (numberOfTheUniverse != 42) {
            throw new Exception("Number of the universe must be 42");
        }
        return null;
    }
}
