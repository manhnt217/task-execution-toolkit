package io.github.manhnt217.task.core.context;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.TaskLogger;
import io.github.manhnt217.task.core.container.FutureProcessor;
import io.github.manhnt217.task.core.exception.inner.ContextException;
import io.github.manhnt217.task.core.repo.EngineRepository;
import io.github.manhnt217.task.core.type.ObjectRef;

import java.util.Iterator;
import java.util.Map;

/**
 * @author manh nguyen
 */
public abstract class AbstractSubActivityContext extends AbstractActivityContext {

    protected final ActivityContext parent;

    protected AbstractSubActivityContext(ActivityContext parent) {
        super(null);
        if (parent == null) {
            throw new IllegalArgumentException("Parent context is null");
        }
        this.parent = parent;
        Iterator<Map.Entry<String, JsonNode>> iterator = parent.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = iterator.next();
            contextParams.set(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public EngineRepository getRepo() {
        return parent.getRepo();
    }

    @Override
    public String createRef(Object object) {
        return parent.createRef(object);
    }

    @Override
    public String getExecutionId() {
        return parent.getExecutionId();
    }

    @Override
    public void clearRef(String refId) {
        parent.clearRef(refId);
    }

    @Override
    public ObjectRef resolveRef(String refId) throws ContextException {
        return parent.resolveRef(refId);
    }

    @Override
    public TaskLogger getLogger() {
        return parent.getLogger();
    }

    @Override
    public Callstack getCallStack() {
        return parent.getCallStack();
    }

    @Override
    public FutureProcessor getFutureProcessor() {
        return parent.getFutureProcessor();
    }
}
