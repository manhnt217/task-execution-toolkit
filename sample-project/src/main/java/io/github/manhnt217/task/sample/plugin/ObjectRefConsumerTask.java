package io.github.manhnt217.task.sample.plugin;

import io.github.manhnt217.task.core.context.ObjectRef;
import io.github.manhnt217.task.core.task.PluginTask;
import io.github.manhnt217.task.core.task.TaskLogger;

/**
 * @author manhnguyen
 */
public class ObjectRefConsumerTask extends PluginTask<ObjectRef, Object> {

    public ObjectRefConsumerTask(String name) {
        super(name);
    }

    @Override
    protected Class<? extends ObjectRef> getInputClass() {
        return ObjectRef.class;
    }

    @Override
    public Object exec(ObjectRef input, TaskLogger taskLogger) throws Exception {
        ObjectRefProducerTask.FortyTwo fortyTwo = input.get();
        int numberOfTheUniverse = fortyTwo.getNumberOfTheUniverse();
        if (numberOfTheUniverse != 42) {
            throw new Exception("Number of the universe must be 42");
        }
        return null;
    }
}
