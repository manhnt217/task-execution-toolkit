package io.github.manhnt217.task.task_engine.context;

/**
 * @author manhnguyen
 */
public class SubprocessContext extends ChildActivityContext {
    public SubprocessContext(ActivityContext context) {
        super(context, true);
    }
}
