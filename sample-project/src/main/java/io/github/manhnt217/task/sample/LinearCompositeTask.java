package io.github.manhnt217.task.sample;

import io.github.manhnt217.task.core.activity.Activity;
import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.activity.simple.EndActivity;
import io.github.manhnt217.task.core.activity.simple.StartActivity;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.task.CompositeTask;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Helper class to create a simplified version of @{@link CompositeTask}
 * @author manhnguyen
 */
public class LinearCompositeTask extends CompositeTask {
    public LinearCompositeTask(String name, List<Activity> childActivities) throws ConfigurationException {
        super(name, buildGroup(childActivities));
    }

    private static Group buildGroup(List<Activity> childActivities) throws ConfigurationException {
        Group activityGroup = new Group();
        StartActivity startActivity = new StartActivity(CompositeTask.START_ACTIVITY_NAME);
        EndActivity endActivity = new EndActivity(CompositeTask.END_ACTIVITY_NAME);
        endActivity.setInputMapping(ActivityContext.ALL_SUBTASKS_JSLT);

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
