package io.github.manhnt217.task.core.activity;

import io.github.manhnt217.task.core.activity.group.Group;

import java.util.HashSet;
import java.util.Set;

/**
 * @author manhnguyen
 */
public abstract class AbstractGroupActivity extends AbstractActivity {

    protected final Group activityGroup;

    public AbstractGroupActivity(String name, Group activityGroup) {
        super(name);
        this.activityGroup = activityGroup;
    }

    @Override
    public Set<String> getContainedActivityNames() {
        HashSet<String> names = new HashSet<>();
        names.add(this.getName());
        names.addAll(this.activityGroup.getContainedActivityNames());
        return names;
    }
}
