package io.github.manhnt217.task.task_engine.context;

/**
 * @author manhnguyen
 */
public class TaskContext extends ChildActivityContext {
    public TaskContext(ActivityContext context) {
        super(context, true);
    }
}
