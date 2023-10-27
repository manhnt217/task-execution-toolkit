package io.github.manhnt217.task.sample;

import io.github.manhnt217.task.task_engine.activity.Activity;
import io.github.manhnt217.task.task_engine.activity.impl.group.Group;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author manhnguyen
 */
public class LinearGroup extends Group {
    public LinearGroup(String name, String startActivityName, String endActivityName, String outputMapping, List<Activity> childActivities) throws ConfigurationException {
        super(name, startActivityName, endActivityName, outputMapping);

        if (CollectionUtils.isEmpty(childActivities)) {
            return;
        }
        this.linkActivities(startActivity, childActivities.get(0), null);
        for (int i = 0; i < childActivities.size() - 1; i++) {
            this.linkActivities(childActivities.get(i), childActivities.get(i + 1), null);
        }
        linkActivities(childActivities.get(childActivities.size() - 1), endActivity, null);
    }
}
