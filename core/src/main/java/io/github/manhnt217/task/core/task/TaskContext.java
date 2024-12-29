package io.github.manhnt217.task.core.task;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.TaskLogger;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.context.RootActivityContext;
import io.github.manhnt217.task.core.context.AbstractSubActivityContext;
import io.github.manhnt217.task.core.repo.EngineRepository;

import java.util.UUID;

/**
 * @author manh nguyen
 */
public class TaskContext extends AbstractSubActivityContext {

    private String taskName;

    public TaskContext(ActivityContext parentContext) {
        super(parentContext, false);
    }

    public TaskContext(String executionId, JsonNode props, EngineRepository repo, TaskLogger logger) {
        this(new RootActivityContext(executionId, props, repo, logger));
    }

    public TaskContext(JsonNode props, EngineRepository repo, TaskLogger logger) {
        this(UUID.randomUUID().toString(), props, repo, logger);
    }

    @Override
    public String getCurrentTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
