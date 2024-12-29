package io.github.manhnt217.task.sample;

import io.github.manhnt217.task.task_engine.activity.Activity;
import io.github.manhnt217.task.task_engine.activity.group.GroupActivity;
import io.github.manhnt217.task.task_engine.activity.group.Group;
import io.github.manhnt217.task.task_engine.activity.simple.EndActivity;
import io.github.manhnt217.task.task_engine.activity.simple.StartActivity;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author manhnguyen
 */
public class LinearGroupActivity extends GroupActivity {
    public LinearGroupActivity(String name, String startActivityName, String endActivityName, String outputMapping, List<Activity> childActivities) throws ConfigurationException {
        super(name, buildGroup(startActivityName, endActivityName, outputMapping, childActivities));

    }

    private static Group buildGroup(String startActivityName, String endActivityName, String outputMapping, List<Activity> childActivities) throws ConfigurationException {
        Group activityGroup = new Group();
        StartActivity startActivity = new StartActivity(startActivityName);
        EndActivity endActivity = new EndActivity(endActivityName);
        endActivity.setInputMapping(outputMapping);

        if (CollectionUtils.isEmpty(childActivities)) {
            // basically do nothing. Just to make sure the group can be executed properly
            activityGroup.linkActivities(startActivity, endActivity, null);
        }

        activityGroup.linkActivities(startActivity, childActivities.get(0), null);
        for (int i = 0; i < childActivities.size() - 1; i++) {
            activityGroup.linkActivities(childActivities.get(i), childActivities.get(i + 1), null);
        }
        activityGroup.linkActivities(childActivities.get(childActivities.size() - 1), endActivity, null);

        return activityGroup;
    }
}
