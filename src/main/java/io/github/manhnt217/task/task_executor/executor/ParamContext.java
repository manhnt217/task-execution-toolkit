package io.github.manhnt217.task.task_executor.executor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.task_executor.task.Task;

public class ParamContext {

	public static final String DOT = ".";
	public static final String KEY_PARENT = "_PARENT_";
	public static final String KEY_IN = "in";
	public static final String KEY_OUT = "out";

	/**
	 * Capture all the inputs & outputs of each task
	 */
	private final ObjectNode contextParams; // root object to evaluate JSLT expression

	public ParamContext() {
		this.contextParams = TaskExecutor.om.createObjectNode();
	}

	public static String getTaskInputPath(Task task) {
		return DOT + task.getId() + DOT + KEY_IN;
	}

	public static String getTaskOutputPath(Task task) {
		return DOT + task.getId() + DOT + KEY_OUT;
	}

    public void saveTaskInput(Task task, JsonNode input) {
		ObjectNode inputNode = TaskExecutor.om.createObjectNode();
		inputNode.set(KEY_IN, input);
		contextParams.set(task.getId(), inputNode);
	}

	public void saveTaskOutput(Task task, JsonNode output) {
		((ObjectNode) contextParams.get(task.getId())).set(KEY_OUT, output);
	}

	public void setGlobalInput(JsonNode globalParams) {
		this.contextParams.set(KEY_PARENT, globalParams);
	}

	public JsonNode getGlobalInput() {
		return this.contextParams.get(KEY_PARENT);
	}
//
//	public JsonNode applyTransform(String jsltExp) {
//		return JSLTUtil.applyTransform(jsltExp, contextParams);
//	}
//
//
//	public JsonNode getContextParams() {
//		return contextParams;
//	}

	public JsonNode transformInput(Task task) {
		String expr = task.getInputMappingExpression();

		if (task.getInputType() == null || task.getInputType() == Task.InputType.NONE) {
			return NullNode.getInstance();
		} else if (Task.InputType.PARENT.equals(task.getInputType())) {
			return JSLTUtil.applyTransform(expr, this.getGlobalInput());
		} else if (Task.InputType.PREVIOUS_TASK.equals(task.getInputType())) {
            if (task.getDependencies().size() == 1) {
                return this.applyFromTaskOutput(task.getDependencies().iterator().next(), expr);
            } else {
                throw new TaskExecutionException("A task should depend on ONLY ONE task when its InputType = Previous Task");
            }
        } else /*if (Task.InputType.GLOBAL == task.getInputType())*/{
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
		return JSLTUtil.applyTransform(jsltExp, jsonNode.get(KEY_OUT));
	}
}
