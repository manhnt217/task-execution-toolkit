package io.github.manhnt217.task.task_engine.persistence.builder;

import io.github.manhnt217.task.task_engine.activity.Activity;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import io.github.manhnt217.task.task_engine.task.CompositeTask;

import static io.github.manhnt217.task.task_engine.task.CompositeTask.*;

/**
 * @author manhnguyen
 */
public class CompositeTaskBuilder extends AbstractTaskBuilder<CompositeTask, CompositeTaskBuilder> {

    private GroupBuilder groupBuilder;

    public CompositeTaskBuilder(String name) {
        this.name = name;
        groupBuilder = new GroupBuilder();
        groupBuilder.start(START_ACTIVITY_NAME);
        groupBuilder.end(END_ACTIVITY_NAME);
    }

    @Override
    public CompositeTask build() throws ConfigurationException {
        groupBuilder.validate();
        return new CompositeTask(name, groupBuilder.buildGroup());
    }

    public CompositeTaskBuilder linkFromStart(Activity a, String guard) {
        this.groupBuilder.linkFromStart(a, guard);
        return this;
    }

    public CompositeTaskBuilder linkFromStart(Activity a) {
        this.groupBuilder.linkFromStart(a);
        return this;
    }

    public CompositeTaskBuilder linkToEnd(Activity a, String guard) {
        this.groupBuilder.linkToEnd(a, guard);
        return this;
    }

    public CompositeTaskBuilder linkToEnd(Activity a) {
        this.groupBuilder.linkToEnd(a);
        return this;
    }

    public CompositeTaskBuilder link(Activity a, Activity b, String guard) {
        this.groupBuilder.link(a, b, guard);
        return this;
    }

    public CompositeTaskBuilder link(Activity a, Activity b) {
        this.groupBuilder.link(a, b);
        return this;
    }

    public CompositeTaskBuilder outputMapping(String outputMapping) {
        this.groupBuilder.outputMapping(outputMapping);
        return this;
    }

}
