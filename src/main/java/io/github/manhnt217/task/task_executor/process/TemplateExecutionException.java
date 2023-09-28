package io.github.manhnt217.task.task_executor.process;

import com.fasterxml.jackson.databind.JsonNode;

public class TemplateExecutionException extends Exception {

	public TemplateExecutionException(String message) {
		super(message);
	}

	public TemplateExecutionException(String templateName, JsonNode input, Exception e) {
		super("Could not execute template '" + templateName + "'. Input = " + input.toString(), e);
	}
}
