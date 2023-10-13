package io.github.manhnt217.task.task_executor.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.task_executor.activity.Activity;
import io.github.manhnt217.task.task_executor.activity.OutboundMessage;

public class ParamContext implements ExecContext {

    private static final String KEY_PARENT = "_PARENT_";
    public static final String WITHOUT_PARENT_JSLT = "{\"" + KEY_PARENT + "\": null, * : . }";

    /**
     * Capture all the inputs & outputs of each task
     */
    private final ObjectNode contextParams; // root object to evaluate JSLT expression

    public ParamContext() {
        this.contextParams = Task.OBJECT_MAPPER.createObjectNode();
    }

    @Override
    public void saveOutput(Activity activity, OutboundMessage output) {
        if (activity instanceof Task) {
            contextParams.set(activity.getName(), output.getContent());
        } else {
            throw new UnsupportedOperationException("Not implemented yet");
        }
    }

    @Override
    public void setParentInput(JsonNode globalParams) {
        this.contextParams.set(KEY_PARENT, globalParams);
    }

    @Override
    public JsonNode transformInput(Task task) {
        String expr = task.getInputMappingExpression();
        return JSLTUtil.applyTransform(expr, contextParams);
    }

    @Override
    public JsonNode transform(String jslt) {
        return JSLTUtil.applyTransform(jslt, contextParams);
    }
}
