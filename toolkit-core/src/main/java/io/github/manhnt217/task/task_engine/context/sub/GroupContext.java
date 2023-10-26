package io.github.manhnt217.task.task_engine.context.sub;

import io.github.manhnt217.task.task_engine.context.ActivityContext;

/**
 * @author manhnguyen
 */
public class GroupContext extends AbstractSubActivityContext {
    public GroupContext(ActivityContext context) {
        super(context, true);
    }
}
