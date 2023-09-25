package io.github.manhnt217.task.task_executor.process;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class TemplateExecutionException extends Exception {
	public TemplateExecutionException(Exception e) {
		super("Cannot execute template. Caused by: " + ExceptionUtils.getRootCauseMessage(e));
	}

	public TemplateExecutionException(String message) {
		super(message);
	}

	public TemplateExecutionException(String message, Exception e) {
		super(message + "\nCaused by: " + ExceptionUtils.getRootCauseMessage(e));
	}
}
