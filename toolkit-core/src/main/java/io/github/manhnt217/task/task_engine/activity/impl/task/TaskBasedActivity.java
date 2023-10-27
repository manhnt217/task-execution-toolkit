package io.github.manhnt217.task.task_engine.activity.impl.task;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_engine.activity.*;
import io.github.manhnt217.task.task_engine.activity.impl.SimpleOutboundMessage;
import io.github.manhnt217.task.task_engine.activity.impl.simple.AbstractSimpleActivity;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.exception.ActivityException;
import io.github.manhnt217.task.task_engine.context.sub.TaskContext;
import io.github.manhnt217.task.task_engine.task.Task;
import io.github.manhnt217.task.task_engine.exception.TaskException;
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
    public OutboundMessage process(InboundMessage in, ActivityLogger activityLogger, ActivityContext context) throws ActivityException {
        JsonNode input = in.getContent();
        try {
            JsonNode output = task.run(input, this.getName(), activityLogger, new TaskContext(context));
            return SimpleOutboundMessage.of(output);
        } catch (TaskException e) {
            throw new ActivityException(this, e);
        }
    }
}
