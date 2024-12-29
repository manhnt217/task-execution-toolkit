package io.github.manhnt217.task.core.task;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.core.activity.TaskLogger;
import io.github.manhnt217.task.core.container.FutureProcessor;
import io.github.manhnt217.task.core.context.AbstractRootActivityContext;
import io.github.manhnt217.task.core.repo.EngineRepository;

import java.util.UUID;

/**
 * @author manh nguyen
 */
public class TaskContext extends AbstractRootActivityContext {

    private String taskName;

    public TaskContext(String executionId, ObjectNode props, EngineRepository repo, FutureProcessor futureProcessor, TaskLogger logger) {
        super(executionId, props, repo, logger, futureProcessor);
    }

    public TaskContext(ObjectNode props, EngineRepository repo, FutureProcessor futureProcessor, TaskLogger logger) {
        this(UUID.randomUUID().toString(), props, repo, futureProcessor, logger);
    }

    public TaskContext(ObjectNode props, EngineRepository repo, TaskLogger logger) {
        this(UUID.randomUUID().toString(), props, repo, null, logger);
    }

    @Override
    public String getCurrentTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}