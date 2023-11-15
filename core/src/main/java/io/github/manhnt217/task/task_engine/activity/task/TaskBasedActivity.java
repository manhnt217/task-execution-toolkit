package io.github.manhnt217.task.task_engine.activity.task;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_engine.activity.AbstractActivity;
import io.github.manhnt217.task.task_engine.activity.Activity;
import io.github.manhnt217.task.task_engine.activity.ActivityLogger;
import io.github.manhnt217.task.task_engine.activity.InboundMessage;
import io.github.manhnt217.task.task_engine.activity.OutboundMessage;
import io.github.manhnt217.task.task_engine.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.context.sub.TaskContext;
import io.github.manhnt217.task.task_engine.exception.ActivityException;
import io.github.manhnt217.task.task_engine.exception.TaskException;
import io.github.manhnt217.task.task_engine.task.Task;
import lombok.Setter;

/**
 * @author manhnguyen
 */
@Setter
public class TaskBasedActivity extends AbstractActivity {

    private final String taskName;

    public TaskBasedActivity(String name, String taskName) {
        super(name);
        this.taskName = taskName;
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityLogger activityLogger, ActivityContext context) throws ActivityException {
        JsonNode input = in.getContent();
        try {
            Task task = context.resolveTask(taskName);
            if (task == null) {
                throw new TaskException(taskName, "Task '" + taskName + "' is not found");
            }
            JsonNode output = task.run(input, this.getName(), activityLogger, new TaskContext(context));
            return SimpleOutboundMessage.of(output);
        } catch (TaskException e) {
            throw new ActivityException(this, e);
        }
    }
}
