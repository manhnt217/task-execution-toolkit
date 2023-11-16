package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.task.CompositeTask;

import static io.github.manhnt217.task.core.task.CompositeTask.*;

/**
 * @author manhnguyen
 */
public class CompositeTaskBuilder extends AbstractTaskBuilder<CompositeTask> implements LinkedActivityGroupBuilder<CompositeTaskBuilder> {

    private GroupBuilder groupBuilder;

    CompositeTaskBuilder(String name) {
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

    @Override
    public GroupBuilder getGroupBuilder() {
        return this.groupBuilder;
    }
}
