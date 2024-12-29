package io.github.manhnt217.task.task_engine.context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.task_engine.activity.Activity;
import io.github.manhnt217.task.task_engine.exception.inner.ContextException;
import io.github.manhnt217.task.task_engine.activity.OutboundMessage;
import io.github.manhnt217.task.task_engine.exception.inner.TransformException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * @author manhnguyen
 */
public class BasicActivityContext implements ActivityContext {

    /**
     * Capture all the inputs & outputs of each task
     */
    protected final ObjectNode contextParams; // root object to evaluate JSLT expression
    private final String executionId;
    protected JsonNode props;

    private final Map<String, Object> objectSpace;

    public BasicActivityContext(String executionId, JsonNode props) {
        this.executionId = executionId;
        this.contextParams = JSONUtil.OBJECT_MAPPER.createObjectNode();
        if (props != null) {
            this.props = props;
            this.contextParams.set(KEY_PROPS, this.props);
        }
        this.objectSpace = new HashMap<>();
    }

    protected BasicActivityContext(String executionId) {
        this(executionId, null);
    }

    @Override
    public void saveOutput(Activity activity, OutboundMessage output) throws ContextException {
        if (activity.registerOutput() && output != null && !output.isEmpty()) {
            if (contextParams.get(activity.getName()) != null) {
                throw new ContextException("Output of activity '" + activity.getName() + "' has already existed in the context");
            }
            contextParams.set(activity.getName(), output.getContent());
        }
    }

    @Override
    public Map<String, JsonNode> toMap() {
        HashMap<String, JsonNode> result = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = contextParams.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override
    public JsonNode transformInput(Activity activity) throws TransformException {
        return JSONUtil.applyTransform(activity.getInputMapping(), contextParams);
    }

    @Override
    public boolean evaluate(String jslt) throws TransformException {
        return JSONUtil.applyTransform(jslt, contextParams).asBoolean();
    }

    @Override
    public String getExecutionId() {
        return executionId;
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
}
