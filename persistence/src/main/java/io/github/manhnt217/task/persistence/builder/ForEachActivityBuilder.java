package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.activity.loop.ForEachActivity;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;

/**
 * @author manh nguyen
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
