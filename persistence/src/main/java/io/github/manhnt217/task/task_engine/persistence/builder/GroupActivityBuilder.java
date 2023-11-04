package io.github.manhnt217.task.task_engine.persistence.builder;

import io.github.manhnt217.task.task_engine.activity.group.Group;
import io.github.manhnt217.task.task_engine.activity.group.GroupActivity;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;

/**
 * @author manhnguyen
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
