package io.github.manhnt217.task.sample.plugin;

import io.github.manhnt217.task.task_engine.type.ObjectRef;
import io.github.manhnt217.task.task_engine.task.PluginTask;
import io.github.manhnt217.task.task_engine.task.TaskLogger;

/**
 * @author manhnguyen
 */
public class ObjectRefProducerTask extends PluginTask<Object, ObjectRef> {

    public ObjectRefProducerTask(String name) {
        super(name);
    }

    @Override
    protected Class<?> getInputClass() {
        return Object.class;
    }

    @Override
    public ObjectRef exec(Object input, TaskLogger taskLogger) throws Exception {
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
