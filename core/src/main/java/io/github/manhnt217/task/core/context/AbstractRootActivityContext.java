package io.github.manhnt217.task.core.context;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.core.activity.TaskLogger;
import io.github.manhnt217.task.core.exception.inner.ContextException;
import io.github.manhnt217.task.core.repo.EngineRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author manh nguyen
 */
public abstract class AbstractRootActivityContext extends AbstractActivityContext {

    // TODO: Consider using @org.apache.commons.collections.bidimap.DualHashBidiMap for faster lookup
    private final Map<String, Object> objectSpace;
    private final String executionId;
    private final EngineRepository repo;
    private final TaskLogger logger;

    public AbstractRootActivityContext(String executionId, ObjectNode props, EngineRepository repo, TaskLogger logger) {
        super(props);
        this.executionId = executionId;
        this.repo = repo;
        this.logger = logger;
        this.objectSpace = new HashMap<>();
    }

    @Override
    public EngineRepository getRepo() {
        return repo;
    }

    @Override
    public String createRef(Object object) {

        for (String id : objectSpace.keySet()) {
            if (objectSpace.get(id) == object) {
                return id;
            }
        }
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
}
