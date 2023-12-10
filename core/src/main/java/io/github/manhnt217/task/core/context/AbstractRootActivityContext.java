package io.github.manhnt217.task.core.context;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.core.activity.TaskLogger;
import io.github.manhnt217.task.core.container.DefaultFutureProcessor;
import io.github.manhnt217.task.core.container.FutureProcessor;
import io.github.manhnt217.task.core.exception.inner.ContextException;
import io.github.manhnt217.task.core.repo.EngineRepository;
import io.github.manhnt217.task.core.type.ObjectRef;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author manh nguyen
 */
public abstract class AbstractRootActivityContext extends AbstractActivityContext {

    private final Map<String, Object> objectSpace;
    private final String executionId;
    private final EngineRepository repo;
    private final TaskLogger logger;
    private final FutureProcessor futureProcessor;

    public AbstractRootActivityContext(String executionId, ObjectNode props, EngineRepository repo, TaskLogger logger, FutureProcessor futureProcessor) {
        super(props);
        this.executionId = executionId;
        this.repo = repo;
        this.logger = logger;
        // Maybe lazy
        if (futureProcessor != null) {
            this.futureProcessor = futureProcessor;
        } else {
            this.futureProcessor = new DefaultFutureProcessor(Runtime.getRuntime().availableProcessors() * 8);
        }
        this.objectSpace = new HashMap<>();
    }

    @Override
    public EngineRepository getRepo() {
        return repo;
    }

    @Override
    public String createRef(Object object) {

        // TODO: Consider using @org.apache.commons.collections.bidimap.DualHashBidiMap for faster lookup
        for (String id : objectSpace.keySet()) {
            if (objectSpace.get(id) == object) {
                return id;
            }
        }
        // TODO: A better ID generator?
        String refId = UUID.randomUUID().toString();
        objectSpace.put(refId, object);
        return refId;
    }

    @Override
    public synchronized void clearRef(String refId) {
        this.objectSpace.remove(refId);
    }

    @Override
    public synchronized ObjectRef resolveRef(String refId) throws ContextException {
        Object o = this.objectSpace.get(refId);
        if (o == null) {
            throw new ContextException("Cannot resolve ObjectRef. Object not found");
        }
        try {
            return new ObjectRef(o);
        } catch (ClassCastException e) {
            throw new ContextException("Cannot cast object of type '" + o.getClass().getName() + "' to desired type ");
        }
    }

    @Override
    public String getExecutionId() {
        return this.executionId;
    }

    @Override
    public TaskLogger getLogger() {
        return this.logger;
    }

    @Override
    public String getCurrentTaskName() {
        return null;
    }

    @Override
    public FutureProcessor getFutureProcessor() {
        return futureProcessor;
    }
}
