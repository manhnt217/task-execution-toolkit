package io.github.manhnt217.task.task_executor.task;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import io.github.manhnt217.task.task_executor.process.ExecutionLog;
import io.github.manhnt217.task.task_executor.process.Template;
import io.github.manhnt217.task.task_executor.process.TemplateExecutionException;

@Getter @Setter
public class TemplateTask extends Task {
	private String templateName;

	@Override
	public JsonNode process(JsonNode inputJSON) {

		try {
			JsonNode output = Template.run(this.getTemplateName(), inputJSON, (severity, message) -> logs.add(new ExecutionLog(severity, message)));
			return transformOutput(output);
		} catch (TemplateExecutionException e) {
			throw new TaskExecutionException(e);
		}
	}

	protected JsonNode transformOutput(JsonNode output) {
		return JSLTUtil.applyTransform(outputMappingExpression, output);
	}
}
