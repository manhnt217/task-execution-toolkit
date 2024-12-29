package io.github.manhnt217.task.task_executor.task;

import io.github.manhnt217.task.task_executor.activity.ActivityException;
import io.github.manhnt217.task.task_executor.process.TemplateExecutionException;
import lombok.Getter;

@Getter
public class TaskExecutionException extends ActivityException {

	private final Task task;

	public TaskExecutionException(Task task, TemplateExecutionException e) {
		super("Task " + task.getName() + " cannot be executed because: An an error occurred in template '" + e.getTemplateName() + "'", e);
		this.task = task;
	}


	public TaskExecutionException(String message, Task task, Exception e) {
		super("Task " + task.getName() + " cannot be executed because: " + message, e);
		this.task = task;
	}
}
