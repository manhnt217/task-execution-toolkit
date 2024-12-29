package io.github.manhnt217.task.task_executor.executor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.task_executor.task.Task;

public class ParamContext {

	public static final String DOT = ".";
	public static final String KEY_GLOBAL_SPACE = "_";
	public static final String KEY_IN = "in";
	public static final String KEY_OUT = "out";
	public static final String EXP_INIT_PARAMS = DOT + KEY_GLOBAL_SPACE + DOT + KEY_IN;
	private final ObjectNode globalSpace;

	/**
	 * Capture all the inputs & outputs of each task
	 */
	private final ObjectNode contextParams; // root object to evaluate JSLT expression

	public ParamContext() {
		this.contextParams = TaskExecutor.om.createObjectNode();
		this.globalSpace = TaskExecutor.om.createObjectNode();
		this.contextParams.set(KEY_GLOBAL_SPACE, globalSpace);
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

	public void saveGlobalInput(JsonNode initParams) {
		globalSpace.set(KEY_IN, initParams);
	}

	public JsonNode applyTransform(String jsltExp) {
		return JSLTUtil.applyTransform(jsltExp, contextParams);
	}

	public JsonNode applyFromTaskOutput(String outputTaskId, String jsltExp) {
		JsonNode jsonNode = contextParams.get(outputTaskId);
		if (jsonNode == null) {
			throw new IllegalArgumentException("Task '" + outputTaskId + "' is not found in the execution context");
		}
		return JSLTUtil.applyTransform(jsltExp, jsonNode.get(KEY_OUT));
	}
}
