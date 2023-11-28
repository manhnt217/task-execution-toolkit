package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.activity.group.GroupActivity;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;

/**
 * @author manh nguyen
 */
public class GroupActivityBuilder extends ContainerActivityBuilder<GroupActivity, GroupActivityBuilder> {
    GroupActivityBuilder() {
        super();
    }

    @Override
    public GroupActivity build() throws ConfigurationException {
        validate();
        Group group = this.groupBuilder.buildGroup();
        GroupActivity groupActivity = new GroupActivity(name, group);
        groupActivity.setInputMapping(inputMapping);
        return groupActivity;
    }
}
