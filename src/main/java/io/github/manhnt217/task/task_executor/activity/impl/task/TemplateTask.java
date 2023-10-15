package io.github.manhnt217.task.task_executor.activity.impl.task;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.activity.Logger;
import io.github.manhnt217.task.task_executor.template.Template;
import io.github.manhnt217.task.task_executor.template.TemplateExecutionException;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TemplateTask extends Task {
	private String templateName;

	public TemplateTask(String name) {
		super(name);
	}

	@Override
	public JsonNode execute(JsonNode input, String executionId, Logger logger, JsonNode props) throws TaskExecutionException {
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
