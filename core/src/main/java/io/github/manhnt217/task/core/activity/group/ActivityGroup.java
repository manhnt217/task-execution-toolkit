package io.github.manhnt217.task.core.activity.group;

import io.github.manhnt217.task.core.activity.Activity;
import io.github.manhnt217.task.core.activity.ActivityLogger;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.GroupException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;

import java.util.Set;

/**
 * @author manhnguyen
 */
public interface ActivityGroup<P, Q> {
    void addActivity(Activity activity) throws ConfigurationException;
    Set<String> getContainedActivityNames();
    void removeActivity(Activity activity) throws ConfigurationException;

    Q execute(P input, ActivityLogger activityLogger, ActivityContext context) throws GroupException, ActivityException;
}
