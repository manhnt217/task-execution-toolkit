package io.github.manhnt217.task.task_executor.task;

import io.github.manhnt217.task.task_executor.process.TemplateExecutionException;

public class TaskExecutionException extends RuntimeException {
	public TaskExecutionException(String message) {
		super(message);
	}

	public TaskExecutionException(TemplateExecutionException e) {
		super(e);
	}

	public TaskExecutionException(String message, Exception e) {
		super(message, e);
	}
}
