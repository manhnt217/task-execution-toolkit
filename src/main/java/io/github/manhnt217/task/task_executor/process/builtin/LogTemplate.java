package io.github.manhnt217.task.task_executor.process.builtin;

import io.github.manhnt217.task.task_executor.process.LogHandler;
import io.github.manhnt217.task.task_executor.process.Severity;
import io.github.manhnt217.task.task_executor.process.Template;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogTemplate extends Template<LogTemplate.LogDto, Object> {

	@Override
	protected Class<? extends LogDto> getInputClass() {
		return LogDto.class;
	}

	@Override
	public Object exec(LogDto input, LogHandler log) {
		log.log(input.getSeverity(), input.getMessage());
		System.out.println("We have just logged this one: " + input);
		return new Object();
	}

	@NoArgsConstructor
	@Data
	public static class LogDto {
		private Severity severity;
		private String message;
	}
}
