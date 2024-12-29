package io.github.manhnt217.task.task_engine.persistence.builder;

import io.github.manhnt217.task.task_engine.activity.group.Group;
import io.github.manhnt217.task.task_engine.activity.loop.ForEachActivity;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;

/**
 * @author manhnguyen
 */
public class ForEachActivityBuilder extends ContainerActivityBuilder<ForEachActivity, ForEachActivityBuilder> {
    ForEachActivityBuilder() {
        super();
    }

    @Override
    public ForEachActivity build() throws ConfigurationException {
        validate();
        Group group = this.groupBuilder.buildGroup();
        ForEachActivity forEachActivity = new ForEachActivity(this.name, group);
        forEachActivity.setInputMapping(inputMapping);
        return forEachActivity;
    }
}
