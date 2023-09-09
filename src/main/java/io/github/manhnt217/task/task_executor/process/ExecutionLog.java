package io.github.manhnt217.task.task_executor.process;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@Getter
public class ExecutionLog {

	private final OffsetDateTime logTime;
	private final String content;
	private final Severity severity;

	public ExecutionLog(Severity severity, String content) {
		this.logTime = OffsetDateTime.now(ZoneId.systemDefault());
		this.content = content;
		this.severity = severity;
	}
}
