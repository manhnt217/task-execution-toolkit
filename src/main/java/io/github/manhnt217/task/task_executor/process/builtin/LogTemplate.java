package io.github.manhnt217.task.task_executor.process.builtin;

import io.github.manhnt217.task.task_executor.process.Severity;
import io.github.manhnt217.task.task_executor.process.Template;
import io.github.manhnt217.task.task_executor.process.TemplateLogHandler;
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
	public Object exec(LogDto input, TemplateLogHandler logHandler) {
		logHandler.log(input.getSeverity(), input.getMessage());
		switch (input.getSeverity()) {
			case INFO:
				log.info(input.getMessage());
				break;
			case WARN:
				log.warn(input.getMessage());
				break;
			case ERROR:
				log.error(input.getMessage());
				break;
			default:
				break;
		}
		return new Object();
	}

	@NoArgsConstructor
	@Data
	public static class LogDto {
		private Severity severity;
		private String message;
	}
}
