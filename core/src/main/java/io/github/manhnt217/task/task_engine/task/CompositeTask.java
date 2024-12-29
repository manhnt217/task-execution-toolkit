package io.github.manhnt217.task.task_engine.task;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_engine.activity.ActivityLogger;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.exception.ActivityException;
import io.github.manhnt217.task.task_engine.exception.GroupException;
import io.github.manhnt217.task.task_engine.activity.group.Group;
import io.github.manhnt217.task.task_engine.exception.TaskException;
import lombok.Getter;

/**
 * A task that contains multiple activities being linked together
 * @author manhnguyen
 */
@Getter
public class CompositeTask implements Task {

    public static final String START_ACTIVITY_NAME = "START";
    public static final String END_ACTIVITY_NAME = "END";
    private final String name;

    private final Group activityGroup;

    public CompositeTask(String name, Group activityGroup) {
        this.name = name;
        this.activityGroup = activityGroup;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public JsonNode run(JsonNode input, String activityName, ActivityLogger activityLogger, ActivityContext context) throws TaskException {
        try {
            return activityGroup.execute(input, activityLogger, context);
        } catch (GroupException e) {
            throw new TaskException(getName(), input, e);
        } catch (ActivityException e) {
            throw new TaskException(getName(), input, e);
        }
    }
}
