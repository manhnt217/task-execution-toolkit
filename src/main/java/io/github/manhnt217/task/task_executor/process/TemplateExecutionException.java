package io.github.manhnt217.task.task_executor.process;

public class TemplateExecutionException extends Exception {
	public TemplateExecutionException(Exception e) {
		super(e);
	}

	public TemplateExecutionException(String message) {
		super(message);
	}

	public TemplateExecutionException(String message, Exception e) {
		super(message, e);
	}
}
