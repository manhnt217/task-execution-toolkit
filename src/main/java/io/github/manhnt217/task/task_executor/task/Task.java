package io.github.manhnt217.task.task_executor.task;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
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

	protected String startLogExpression;

	protected String endLogExpression;

	protected Set<String> dependencies = Collections.emptySet();

	/**
	 * Define which source (JSON) to apply the <code>inputMappingExpression</code> (JSLT transformation)
	 */
	public enum InputType {
		NONE, PARENT, PREVIOUS_TASK, GLOBAL
	}
}