package io.github.manhnt217.task.task_executor.process;

public interface LogHandler {
	void log(String executionSessionId, String taskId, Severity severity, String message);
}
