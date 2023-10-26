package io.github.manhnt217.task.task_engine.context;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author manhnguyen
 */
public class SimpleActivityContext extends AbstractActivityContext {

    private final Map<String, Object> objectSpace;
    private final String executionId;

    public SimpleActivityContext(String executionId, JsonNode props) {
        this.executionId = executionId;
        if (props != null) {
            this.contextParams.set(KEY_PROPS, props);
        }
        this.objectSpace = new HashMap<>();
    }

    @Override
    public ObjectRef createRef(Object object) {
        for (String id : objectSpace.keySet()) {
            if (objectSpace.get(id) == object) {
                return new ObjectRef(id);
            }
        }
        String refId = UUID.randomUUID().toString();
        objectSpace.put(refId, object);
        return new ObjectRef(refId);
    }

    @Override
    public void clearRef(ObjectRef objectRef) {
        this.objectSpace.remove(objectRef.getRefId());
    }

    @Override
    public <T> T resolveRef(ObjectRef objectRef, Class<T> type) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public String getExecutionId() {
        return this.executionId;
    }
}
