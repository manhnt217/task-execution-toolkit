package io.github.manhnt217.task.task_executor.task;

public class ExecutionEvent {
	public ExecutionEvent(String taskName, ExecutionEventType eventType) {
	}

	enum ExecutionEventType {
		TASK_START,
		TASK_FAILURE,
		TASK_SUCCESS
	}
}
