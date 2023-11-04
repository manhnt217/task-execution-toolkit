package io.github.manhnt217.task.task_engine.context;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_engine.exception.inner.ContextException;
import io.github.manhnt217.task.task_engine.task.Task;
import io.github.manhnt217.task.task_engine.task.TaskResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author manhnguyen
 */
public class SimpleActivityContext extends AbstractActivityContext {

    // TODO: Consider using @org.apache.commons.collections.bidimap.DualHashBidiMap
    private final Map<String, Object> objectSpace;
    private final String executionId;
    private TaskResolver taskResolver;

    public SimpleActivityContext(String executionId, JsonNode props, TaskResolver taskResolver) {
        this.executionId = executionId;
        this.taskResolver = taskResolver;
        if (props != null) {
            this.contextParams.set(KEY_PROPS, props);
        }
        this.objectSpace = new HashMap<>();
    }

    @Override
    public Task resolveTask(String name) {
        return taskResolver.resolve(name);
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
}
