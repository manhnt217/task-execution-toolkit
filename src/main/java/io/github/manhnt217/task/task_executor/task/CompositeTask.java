package io.github.manhnt217.task.task_executor.task;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.activity.ActivityLogger;
import io.github.manhnt217.task.task_executor.activity.ExecutionException;
import io.github.manhnt217.task.task_executor.activity.impl.LinkBasedActivityGroup;
import io.github.manhnt217.task.task_executor.context.ActivityContext;
import lombok.Getter;

import static io.github.manhnt217.task.task_executor.context.ActivityContext.ALL_SUBTASKS_JSLT;

/**
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
    public JsonNode run(JsonNode input, String activityName, ActivityLogger activityLogger, ActivityContext context) throws TaskExecutionException {
        try {
            return this.execute(input, activityLogger, context);
        } catch (ExecutionException e) {
            throw new TaskExecutionException(getName(), input, "Exception was thrown from inner activities", e);
        }
    }
}
