package io.github.manhnt217.task.core.task;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.core.activity.TaskLogger;
import io.github.manhnt217.task.core.container.FutureProcessor;
import io.github.manhnt217.task.core.context.AbstractRootActivityContext;
import io.github.manhnt217.task.core.context.Callstack;
import io.github.manhnt217.task.core.repo.EngineRepository;

/**
 * @author manh nguyen
 */
public class TaskContext extends AbstractRootActivityContext {

    private String taskName;

    public TaskContext(String executionId, Callstack callstack, ObjectNode props, EngineRepository repo, FutureProcessor futureProcessor, TaskLogger logger) {
        super(executionId, callstack, props, repo, logger, futureProcessor);
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
