package io.github.manhnt217.task.task_engine.context.sub;

import io.github.manhnt217.task.task_engine.context.ActivityContext;

/**
 * @author manhnguyen
 */
public class TaskContext extends AbstractSubActivityContext {
    public TaskContext(ActivityContext context) {
        super(context, false);
    }
}
