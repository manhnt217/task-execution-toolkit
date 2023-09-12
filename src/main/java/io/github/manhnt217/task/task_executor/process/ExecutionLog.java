package io.github.manhnt217.task.task_executor.process;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@Getter
public class ExecutionLog {

	private final String taskId;
	private final OffsetDateTime logTime;
	private final String content;
	private final Severity severity;

	public ExecutionLog(String taskId, Severity severity, String content) {
		this.taskId = taskId;
		this.logTime = OffsetDateTime.now(ZoneId.systemDefault());
		this.content = content;
		this.severity = severity;
	}
}
