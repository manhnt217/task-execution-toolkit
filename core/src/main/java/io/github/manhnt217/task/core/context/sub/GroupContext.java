package io.github.manhnt217.task.core.context.sub;

import io.github.manhnt217.task.core.context.ActivityContext;

/**
 * @author manhnguyen
 */
public class GroupContext extends AbstractSubActivityContext {
    public GroupContext(ActivityContext context) {
        super(context, true);
    }
}
