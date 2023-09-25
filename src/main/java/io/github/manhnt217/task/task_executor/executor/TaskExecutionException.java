package io.github.manhnt217.task.task_executor.executor;

import io.github.manhnt217.task.task_executor.process.TemplateExecutionException;
import io.github.manhnt217.task.task_executor.task.Task;
import lombok.Getter;

@Getter
public class TaskExecutionException extends Exception {

	private final Task task;

	public TaskExecutionException(String message, Task task) {
		super("Task " + task.getId() + " cannot be executed.\nCaused by: " + message);
		this.task = task;
	}

	public TaskExecutionException(TemplateExecutionException e, Task task) {
		super("Task " + task.getId() + " cannot be executed.\nCaused by: " + e.getMessage());
		this.task = task;
	}

	public TaskExecutionException(String message, Exception e, Task task) {
		super("Task " + task.getId() + " cannot be executed.\nCaused by: " + message, e);
		this.task = task;
	}
}
