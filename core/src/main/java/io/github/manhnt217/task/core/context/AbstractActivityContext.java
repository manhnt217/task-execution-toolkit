package io.github.manhnt217.task.core.context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.core.activity.Activity;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.exception.inner.ContextException;
import io.github.manhnt217.task.core.exception.inner.TransformException;

import java.util.Iterator;
import java.util.Map;

/**
 * @author manh nguyen
 */
public abstract class AbstractActivityContext implements ActivityContext {

    /**
     * Store all the outputs of each task
     * Store global properties
     */
    protected final ObjectNode contextParams; // root object to evaluate JSLT expression

    protected final ObjectNode props;

    protected AbstractActivityContext(ObjectNode props) {
        this.props = props;
        this.contextParams = JSONUtil.createObjectNode();
        if (props != null) {
            this.contextParams.set(KEY_PROPS, props);
        }
    }

    @Override
    public ObjectNode getProps() {
        return props;
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
    public Iterator<Map.Entry<String, JsonNode>> iterator() {
        return contextParams.fields();
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
