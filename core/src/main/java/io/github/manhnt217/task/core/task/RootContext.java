package io.github.manhnt217.task.core.task;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.core.activity.TaskLogger;
import io.github.manhnt217.task.core.context.AbstractRootActivityContext;
import io.github.manhnt217.task.core.context.Callstack;
import io.github.manhnt217.task.core.repo.EngineRepository;

import java.util.UUID;

/**
 * @author manhnguyen
 */
public class RootContext extends AbstractRootActivityContext {

    public RootContext(ObjectNode props, EngineRepository repo, TaskLogger logger) {
        super(UUID.randomUUID().toString(), Callstack.root(), props, repo, logger, null);
    }
}
