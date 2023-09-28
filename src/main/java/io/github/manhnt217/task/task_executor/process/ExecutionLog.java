package io.github.manhnt217.task.task_executor.process;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

@Getter
public class ExecutionLog {

	private final String executionSessionId;
	private final String taskId;
	private final OffsetDateTime logTime;
	private final String content;
	private final Severity severity;

	@Getter(AccessLevel.NONE)
	private final Throwable error;

	public ExecutionLog(String executionSessionId, String taskId, Severity severity, String content, Throwable error) {
		this.executionSessionId = executionSessionId;
		this.taskId = taskId;
		this.error = error;
		this.logTime = OffsetDateTime.now(ZoneId.systemDefault());
		this.content = content;
		this.severity = severity;
	}

	@JsonProperty("errorSummary")
	public String getErrorSummary() {
		return ExceptionUtils.getThrowableList(error).stream()
				.map(t -> t.getClass().getName() + ": " + StringUtils.defaultIfBlank(t.getMessage(), ""))
				.collect(Collectors.joining("\nCaused by: "));
	}
}
