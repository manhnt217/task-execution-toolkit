package io.github.manhnt217.task.core.context.sub;

import io.github.manhnt217.task.core.context.ActivityContext;

/**
 * @author manhnguyen
 */
public class TaskContext extends AbstractSubActivityContext {
    public TaskContext(ActivityContext context) {
        super(context, false);
    }
}
