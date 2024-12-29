package io.github.manhnt217.task.task_executor;

import io.github.manhnt217.task.task_executor.activity.Activity;
import io.github.manhnt217.task.task_executor.task.CompositeTask;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Helper class to create a simplified version of @{@link CompositeTask}
 * @author manhnguyen
 */
public class LinearCompositeTask extends CompositeTask {
    public LinearCompositeTask(String name, List<Activity> childActivities) {
        super(name);
        if (CollectionUtils.isEmpty(childActivities)) {
            return;
        }
        this.addActivity(childActivities.get(0));
        this.linkActivities(startActivity, childActivities.get(0), null);
        for (int i = 0; i < childActivities.size() - 1; i++) {
            link(childActivities.get(i), childActivities.get(i + 1));
        }
        linkActivities(childActivities.get(childActivities.size() - 1), endActivity, null);
    }

    protected void link(Activity from, Activity to) {
        this.addActivity(to);
        this.linkActivities(from, to, null);
    }
}
