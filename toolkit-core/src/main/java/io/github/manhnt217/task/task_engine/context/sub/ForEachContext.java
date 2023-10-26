package io.github.manhnt217.task.task_engine.context.sub;

import io.github.manhnt217.task.task_engine.context.ActivityContext;

/**
 * @author manhnguyen
 */
public class ForEachContext extends AbstractSubActivityContext {
    public ForEachContext(ActivityContext context) {
        super(context, true);
    }
}
