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
public abstract class AbstractActivityContext implements ActivityContext {

    /**
     * Store all the outputs of each task
     * Store global properties
     */
    protected final ObjectNode contextParams; // root object to evaluate JSLT expression

    protected AbstractActivityContext() {
        this.contextParams = JSONUtil.OBJECT_MAPPER.createObjectNode();
    }

    @Override
    public void saveOutput(Activity activity, OutboundMessage output) throws ContextException {
        validate(activity);
        if (activity.registerOutput() && output != null && !output.isEmpty()) {
            if (contextParams.get(activity.getName()) != null) {
                throw new ContextException("Output of activity '" + activity.getName() + "' has already existed in the context");
            }
            contextParams.set(activity.getName(), output.getContent());
        }
    }

    private static void validate(Activity activity) throws ContextException {
        if (activity.getName().startsWith("_")) {
            throw new ContextException("Activity's name cannot start with '_'");
        }
        if (activity.getName().contains(".")) {
            throw new ContextException("Activity's name cannot container '.'");
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
}
