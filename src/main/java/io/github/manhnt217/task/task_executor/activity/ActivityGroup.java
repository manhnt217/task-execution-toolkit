package io.github.manhnt217.task.task_executor.activity;

import io.github.manhnt217.task.task_executor.context.ActivityContext;

/**
 * @author manhnguyen
 */
public interface ActivityGroup<P, Q> {
    void addActivity(Activity activity);

    Q execute(P input, ActivityLogger activityLogger, ActivityContext context) throws ExecutionException;
}
