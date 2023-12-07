package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.activity.group.GroupActivity;
import io.github.manhnt217.task.core.activity.group.SyncGroupActivity;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;

/**
 * @author manh nguyen
 */
public class GroupActivityBuilder extends ContainerActivityBuilder<GroupActivity, GroupActivityBuilder> {
    private final boolean synced;

    GroupActivityBuilder(String name, boolean synced) {
        super(name);
        this.synced = synced;
    }

    @Override
    public GroupActivity build() throws ConfigurationException {
        validate();
        Group group = this.groupBuilder.buildGroup();
        GroupActivity groupActivity = synced ? new SyncGroupActivity(name, group) : new GroupActivity(name, group);
        groupActivity.setInputMapping(inputMapping);
        return groupActivity;
    }
}
