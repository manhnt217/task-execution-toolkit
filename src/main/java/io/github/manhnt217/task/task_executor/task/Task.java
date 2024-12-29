package io.github.manhnt217.task.task_executor.task;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter @Setter
public abstract class Task {

	/**
	 * <ul>
	 * 	   <li> Contains alphanumerics, sharps & hyphens ONLY </li>
	 * 	   <li> No 2 or more consecutive sharps, hyphens. </li>
	 * 	   <li> Must start and end with an alphanumeric. </li>
	 * 	   <li> Example: <code>a-package#a-subpackage#some-task-1</code> </li>
	 * </ul>
	 */
	protected String id;

	protected InputType inputType;

	protected String inputMappingExpression;

	protected String outputMappingExpression;

	protected String startLogExpression;

	protected String endLogExpression;

	protected Set<String> dependencies = new HashSet<>(0);

	public boolean isIndependent() {
		return getDependencies().size() == 0;
	}

	public boolean isMonoDependent() {
		return getDependencies().size() == 1;
	}

	//	protected void log(String jslt, TaskExecutionContext ctx) {
//		if (StringUtils.isBlank(jslt)) {
//			return;
//		}
//		try {
//			JsonNode jsonNode = ctx.applyTransform(jslt);
//			ctx.log(Severity.INFO, jsonNode.isContainerNode() ? Main.om.writeValueAsString(jsonNode) : jsonNode.asText());
//		} catch (Exception e) {
//			ctx.log(Severity.WARN, "Error while applying log expression to the context. Expression = " + jslt);
//		}
//	}

	/**
	 * Define which source (JSON) to apply the <code>inputMappingExpression</code> (JSLT transformation)
	 */
	public enum InputType {
		NONE, CONTEXT, PREVIOUS_TASK
	}
}