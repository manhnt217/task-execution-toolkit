package io.github.manhnt217.task.task_engine.activity.group;

import io.github.manhnt217.task.task_engine.activity.Activity;
import io.github.manhnt217.task.task_engine.activity.ActivityLogger;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.exception.ActivityException;
import io.github.manhnt217.task.task_engine.exception.GroupException;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;

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
