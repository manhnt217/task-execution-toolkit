package io.github.manhnt217.task.task_executor.context;

/**
 * @author manhnguyen
 */
public class GroupContext extends ChildActivityContext {
    public GroupContext(ActivityContext context) {
        super(context, false);
    }
}