package io.github.manhnt217.task.task_executor.task;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import io.github.manhnt217.task.task_executor.process.ExecutionLog;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public abstract class Task implements Proc {

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

	@Getter(AccessLevel.NONE)
	protected String outputMappingExpression;

	protected String startLogExpression;

	protected String endLogExpression;

	protected List<String> dependencies = new ArrayList<>(0);

	protected List<ExecutionLog> logs;

	public Task() {
		this.logs = new ArrayList<>(0);
	}

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