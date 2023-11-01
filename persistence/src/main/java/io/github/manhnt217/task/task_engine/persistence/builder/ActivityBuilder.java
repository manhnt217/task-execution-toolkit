package io.github.manhnt217.task.task_engine.persistence.builder;

import io.github.manhnt217.task.task_engine.task.Task;

/**
 * @author manhnguyen
 */
public class ActivityBuilder {

    public static ForEachActivityBuilder forEach() {
        return new ForEachActivityBuilder();
    }

    public static GroupActivityBuilder group() {
        return new GroupActivityBuilder();
    }

    public static TaskBasedActivityBuilder task(String name, Task task) {
        return new TaskBasedActivityBuilder(name, task);
    }

    public static PluginTaskBuilder plugin(String name) {
        return new PluginTaskBuilder(name);
    }

    public static CompositeTaskBuilder composite(String name) {
        return new CompositeTaskBuilder(name);
    }
}
