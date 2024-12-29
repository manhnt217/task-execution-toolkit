package io.github.manhnt217.task.task_executor.process;

public interface Logger {
	void info(String executionSessionId, String taskId, String message);
	void warn(String executionSessionId, String taskId, String message, Throwable e);
	void error(String executionSessionId, String taskId, String message, Throwable e);
}
