package io.github.manhnt217.task.task_executor.activity.impl;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.activity.*;
import io.github.manhnt217.task.task_executor.context.ActivityContext;
import io.github.manhnt217.task.task_executor.context.SubprocessContext;
import io.github.manhnt217.task.task_executor.task.Task;
import io.github.manhnt217.task.task_executor.task.TaskExecutionException;
import lombok.Setter;

/**
 * @author manhnguyen
 */
@Setter
public class TaskBasedActivity extends AbstractSimpleActivity implements Activity {

    private Task task;

    public TaskBasedActivity(String name, Task task) {
        super(name);
        this.task = task;
    }

    public TaskBasedActivity(String name) {
        this(name, null);
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityLogger activityLogger, ActivityContext context) throws ActivityExecutionException {
        JsonNode input = in.getContent();
        try {
            JsonNode output = task.run(input, this.getName(), activityLogger, new SubprocessContext(context));
            return SimpleOutboundMessage.of(output);
        } catch (TaskExecutionException e) {
            throw new ActivityExecutionException(this, e);
        }
    }
}
