package io.github.manhnt217.task.task_engine.task;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_engine.activity.ActivityLogger;
import io.github.manhnt217.task.task_engine.exception.ActivityException;
import io.github.manhnt217.task.task_engine.exception.GroupException;
import io.github.manhnt217.task.task_engine.activity.impl.LinkBasedActivityGroup;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.exception.TaskException;
import lombok.Getter;

import static io.github.manhnt217.task.task_engine.context.ActivityContext.ALL_SUBTASKS_JSLT;

/**
 * A task that contains multiple activities being linked together
 * @author manhnguyen
 */
@Getter
public class CompositeTask extends LinkBasedActivityGroup implements Task {

    public static final String START_ACTIVITY_NAME = "START";
    public static final String END_ACTIVITY_NAME = "END";
    private final String name;

    public CompositeTask(String name, String outputMapping) {
        super(START_ACTIVITY_NAME, END_ACTIVITY_NAME, outputMapping);
        this.name = name;
    }

    public CompositeTask(String name) {
        this(name, ALL_SUBTASKS_JSLT);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public JsonNode run(JsonNode input, String activityName, ActivityLogger activityLogger, ActivityContext context) throws TaskException {
        try {
            return this.execute(input, activityLogger, context);
        } catch (GroupException e) {
            throw new TaskException(getName(), input, e);
        } catch (ActivityException e) {
            throw new TaskException(getName(), input, e);
        }
    }
}
