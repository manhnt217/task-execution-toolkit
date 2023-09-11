package io.github.manhnt217.task.task_executor.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.task_executor.Main;

public class TaskExecutionContext {

	public static final String KEY_GLOBAL_SPACE = "_";
	public static final String KEY_IN = "in";
	public static final String KEY_OUT = "out";
	public static final String EXP_INIT_PARAMS = "." + KEY_GLOBAL_SPACE + "." + KEY_IN;
	private final ObjectNode globalSpace;

	/**
	 * Capture all the inputs & outputs of each task
	 */
	private ObjectNode contextParams; // root object to evaluate JSLT expression

	public TaskExecutionContext() {
		this.contextParams = Main.om.createObjectNode();
		this.globalSpace = Main.om.createObjectNode();
		this.contextParams.set(KEY_GLOBAL_SPACE, globalSpace);
	}

	public void saveInput(Task task, JsonNode input) {
		ObjectNode inputNode = Main.om.createObjectNode();
		inputNode.set(KEY_IN, input);
		contextParams.set(task.getId(), inputNode);
	}

	public void saveOutput(Task task, JsonNode output) {
		((ObjectNode) contextParams.get(task.getId())).set(KEY_OUT, output);
	}

	public JsonNode applyTransform(String jsltExp) {
		return JSLTUtil.applyTransform(jsltExp, contextParams);
	}

	public void saveInitialInput(JsonNode initParams) {
		globalSpace.set(KEY_IN, initParams);
	}

	public JsonNode applyFromTaskOutput(String taskId, String jsltExp) {
		return JSLTUtil.applyTransform(jsltExp, contextParams.get(taskId).get(KEY_OUT));
	}
}
