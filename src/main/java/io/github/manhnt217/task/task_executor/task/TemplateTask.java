package io.github.manhnt217.task.task_executor.task;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.process.Logger;
import io.github.manhnt217.task.task_executor.process.Template;
import io.github.manhnt217.task.task_executor.process.TemplateExecutionException;
import io.github.manhnt217.task.task_executor.process.TemplateLogger;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TemplateTask extends Task {
	private String templateName;

	@Override
	public JsonNode execute(JsonNode input, String executionId, Logger logger) throws TaskExecutionException {
		try {
			return Template.run(
					this.getTemplateName(),
					input,
					new TemplateLogger(executionId, this.getName(), logger));
		} catch (TemplateExecutionException e) {
			throw new TaskExecutionException(this, e);
		} catch (Exception e) {
			throw new TaskExecutionException("An exception was thrown during the task execution", this, e);
		}
	}
}
