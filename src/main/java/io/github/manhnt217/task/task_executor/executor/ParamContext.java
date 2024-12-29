package io.github.manhnt217.task.task_executor.executor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.task_executor.task.Task;

public class ParamContext {

    private static final String DOT = ".";
    private static final String KEY_PARENT = "_PARENT_";
    private static final String WITHOUT_PARENT_JSLT = "{\"" + KEY_PARENT + "\": null, * : . }";

    /**
     * Capture all the inputs & outputs of each task
     */
    private final ObjectNode contextParams; // root object to evaluate JSLT expression
    private String lastTaskId = null;

    public ParamContext() {
        this.contextParams = TaskExecutor.om.createObjectNode();
    }

    public void saveTaskOutput(Task task, JsonNode output) {
        contextParams.set(task.getId(), output);
        lastTaskId = task.getId();
    }

    public void setParentInput(JsonNode globalParams) {
        this.contextParams.set(KEY_PARENT, globalParams);
    }

    public JsonNode transformInput(Task task) throws TaskExecutionException {
        String expr = task.getInputMappingExpression();

        if (task.getInputType() == null || task.getInputType() == Task.InputType.NONE) {
            return NullNode.getInstance();
        } else if (Task.InputType.PARENT.equals(task.getInputType())) {
            return JSLTUtil.applyTransform(expr, this.contextParams.get(KEY_PARENT));
        } else if (Task.InputType.PREVIOUS_TASK.equals(task.getInputType())) {
            if (lastTaskId != null) {
                return this.applyFromTaskOutput(lastTaskId, expr);
            } else {
                throw new TaskExecutionException("The input type for task '" + task.getId() + "' is '" + Task.InputType.PREVIOUS_TASK + "' but no task was executed previously", task);
            }
        } else /*if (Task.InputType.GLOBAL == task.getInputType())*/ {
            return JSLTUtil.applyTransform(expr, contextParams);
        }
    }

    public JsonNode transform(String jslt) {
        return JSLTUtil.applyTransform(jslt, contextParams);
    }

    private JsonNode applyFromTaskOutput(String outputTaskId, String jsltExp) {
        JsonNode jsonNode = contextParams.get(outputTaskId);
        if (jsonNode == null) {
            throw new IllegalArgumentException("Task '" + outputTaskId + "' is not found in the execution context");
        }
        return JSLTUtil.applyTransform(jsltExp, jsonNode);
    }

    public JsonNode allTaskOutputs() {
        return JSLTUtil.applyTransform(WITHOUT_PARENT_JSLT, contextParams);
    }
}
