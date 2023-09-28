package io.github.manhnt217.task.task_executor.executor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.task_executor.task.Task;

public class ParamContext {

    private static final String KEY_PARENT = "_PARENT_";
    private static final String WITHOUT_PARENT_JSLT = "{\"" + KEY_PARENT + "\": null, * : . }";

    /**
     * Capture all the inputs & outputs of each task
     */
    private final ObjectNode contextParams; // root object to evaluate JSLT expression

    public ParamContext() {
        this.contextParams = TaskExecutor.om.createObjectNode();
    }

    public void saveTaskOutput(Task task, JsonNode output) {
        contextParams.set(task.getId(), output);
    }

    public void setParentInput(JsonNode globalParams) {
        this.contextParams.set(KEY_PARENT, globalParams);
    }

    public JsonNode transformInput(Task task) {
        String expr = task.getInputMappingExpression();
        return JSLTUtil.applyTransform(expr, contextParams);
    }

    public JsonNode transform(String jslt) {
        return JSLTUtil.applyTransform(jslt, contextParams);
    }

    public JsonNode allTaskOutputs() {
        return JSLTUtil.applyTransform(WITHOUT_PARENT_JSLT, contextParams);
    }
}
