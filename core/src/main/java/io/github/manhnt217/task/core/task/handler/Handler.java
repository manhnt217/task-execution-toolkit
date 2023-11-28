package io.github.manhnt217.task.core.task.handler;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.activity.simple.StartActivity;
import io.github.manhnt217.task.core.activity.source.FromSourceActivity;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.GroupException;
import io.github.manhnt217.task.core.exception.TaskException;
import io.github.manhnt217.task.core.task.Task;
import io.github.manhnt217.task.core.task.TaskContext;
import lombok.Getter;

/**
 * @author manh nguyen
 */
public class Handler implements Task {

    @Getter
    private final String name;
    @Getter
    private final String sourceName;

    private final Group activityGroup;

    public Handler(String name, Group activityGroup) {
        this.name = name;
        this.activityGroup = activityGroup;
        StartActivity startActivity = activityGroup.getStartActivity();
        if (startActivity instanceof FromSourceActivity) {
            sourceName = ((FromSourceActivity) startActivity).getSourceName();
        } else {
            throw new IllegalArgumentException("Handler's activity group must contain a " + FromSourceActivity.class.getSimpleName());
        }
    }

    public JsonNode handle(JsonNode input, TaskContext context) throws TaskException {
        context.setTaskName(this.name);
        try {
            return activityGroup.execute(input, context);
        } catch (GroupException e) {
            throw new TaskException(getName(), input, e);
        } catch (ActivityException e) {
            throw new TaskException(getName(), input, e);
        }
    }
}
