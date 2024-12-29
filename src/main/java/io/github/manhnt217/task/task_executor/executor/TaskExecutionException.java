package io.github.manhnt217.task.task_executor.executor;

import io.github.manhnt217.task.task_executor.process.TemplateExecutionException;
import io.github.manhnt217.task.task_executor.task.Task;
import lombok.Getter;

@Getter
public class TaskExecutionException extends Exception {

	private final Task task;

	public TaskExecutionException(Task task, TemplateExecutionException e) {
		super("Task " + task.getId() + " cannot be executed", e);
		this.task = task;
	}


	public TaskExecutionException(String message, Task task, Exception e) {
		super("Task " + task.getId() + " cannot be executed.\nCaused by: " + message, e);
		this.task = task;
	}
}
