package io.github.manhnt217.task.core.activity.loop;

import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.context.AbstractSubActivityContext;

/**
 * @author manh nguyen
 */
class ForEachContext extends AbstractSubActivityContext {
    public ForEachContext(ActivityContext context) {
        super(context, true);
    }
}
