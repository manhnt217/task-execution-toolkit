package io.github.manhnt217.task.task_engine.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_engine.activity.ActivityLogger;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.context.JSONUtil;
import io.github.manhnt217.task.task_engine.exception.PluginException;

/**
 * @author manhnguyen
 */
public abstract class PluginTask<P, R> implements Task {

    private final String name;

    public PluginTask(String name) {
        this.name = name;
    }

    @Override
    public final JsonNode run(JsonNode input, String activityName, ActivityLogger activityLogger, ActivityContext context) throws PluginException {
        P in;
        try {
            in = JSONUtil.treeToValue(input, getInputClass(), context);
        } catch (JsonProcessingException e) {
            throw new PluginException(getName(), input, "Cannot convert in to desired type", e);
        }
        R rs;
        try {
            rs = exec(in, new TaskLogger(context.getExecutionId(), activityName, activityLogger));
        } catch (Exception e) {
            throw new PluginException(getName(), input, "Exception while executing task", e);
        }
        return JSONUtil.valueToTree(rs, context);
    }

    public String getName() {
        return this.name;
    }

    protected abstract Class<? extends P> getInputClass();

    public abstract R exec(P input, TaskLogger taskLogger) throws Exception;
}
