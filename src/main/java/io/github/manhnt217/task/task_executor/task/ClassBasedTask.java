package io.github.manhnt217.task.task_executor.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.activity.ActivityLogger;
import io.github.manhnt217.task.task_executor.context.ActivityContext;

import static io.github.manhnt217.task.task_executor.context.ActivityContext.OBJECT_MAPPER;

/**
 * @author manhnguyen
 */
public abstract class ClassBasedTask<P, R> implements Task {

    private final String name;

    public ClassBasedTask(String name) {
        this.name = name;
    }

    @Override
    @SuppressWarnings("unused")
    public final JsonNode run(JsonNode input, String activityName, ActivityLogger activityLogger, ActivityContext context) throws TaskExecutionException {
        P in;
        try {
            in = OBJECT_MAPPER.treeToValue(input, getInputClass());
        } catch (JsonProcessingException e) {
            throw new TaskExecutionException(getName(), input, "Cannot convert in to desired type", e);
        }
        R rs;
        try {
            rs = exec(in, new TaskLogger(context.getExecutionId(), activityName, activityLogger));
        } catch (Exception e) {
            throw new TaskExecutionException(getName(), input, "Exception while executing task", e);
        }
        return OBJECT_MAPPER.valueToTree(rs);
    }

    public String getName() {
        return this.name;
    }

    protected abstract Class<? extends P> getInputClass();

    public abstract R exec(P input, TaskLogger taskLogger) throws Exception;
}
