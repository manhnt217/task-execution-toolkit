package io.github.manhnt217.task.task_executor.activity.impl.task.context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.task_executor.activity.Activity;
import io.github.manhnt217.task.task_executor.activity.OutboundMessage;
import io.github.manhnt217.task.task_executor.activity.impl.task.JSLTUtil;
import io.github.manhnt217.task.task_executor.activity.impl.task.Task;

import java.util.Iterator;
import java.util.Map;

public class ParamContext implements ExecContext {

    public static final String KEY_PROPS = "_PROPS_";

    /**
     * Capture all the inputs & outputs of each task
     */
    private final ObjectNode contextParams; // root object to evaluate JSLT expression
    private final JsonNode props;

    public ParamContext(JsonNode props) {
        this.contextParams = Task.OBJECT_MAPPER.createObjectNode();
        this.props = props;
        this.contextParams.set(KEY_PROPS, this.props);
    }

    private ParamContext(ObjectNode parentContextParam, JsonNode props) {
        this(props);
        copyActivityOutput(parentContextParam);
    }

    private void copyActivityOutput(ObjectNode parentContextParam) {
        Iterator<Map.Entry<String, JsonNode>> fields = parentContextParam.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            if (isActivityOutput(entry)) {
                this.contextParams.set(entry.getKey(), entry.getValue());
            }
        }
    }

    private boolean isActivityOutput(Map.Entry<String, JsonNode> entry) {
        return !KEY_PROPS.equals(entry.getKey());
    }

    @Override
    public void saveOutput(Activity activity, OutboundMessage output) {
        if (activity.registerOutput() && output.getContent() != null) {

            if (contextParams.get(activity.getName()) != null) {
                throw new IllegalStateException("Output of activity '" + activity.getName() + "' has already existed in the context");
            }
            contextParams.set(activity.getName(), output.getContent());
        }
    }

    @Override
    public JsonNode transformInput(Task task) {
        String expr = task.getInputMapping();
        return this.transform(expr);
    }

    @Override
    public JsonNode transform(String jslt) {
        return JSLTUtil.applyTransform(jslt, contextParams);
    }

    @Override
    public ExecContext createChild() {
        return new ParamContext(contextParams, props);
    }

    @Override
    public JsonNode getProps() {
        return props;
    }
}
