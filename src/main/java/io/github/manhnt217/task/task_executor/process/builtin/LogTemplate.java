package io.github.manhnt217.task.task_executor.process.builtin;

import io.github.manhnt217.task.task_executor.process.Severity;
import io.github.manhnt217.task.task_executor.process.Template;
import io.github.manhnt217.task.task_executor.process.TemplateLogger;
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
	public Object exec(LogDto input, TemplateLogger logger) {
		switch (input.getSeverity()) {
			case INFO:
				log.info(input.getMessage());
				logger.info(input.getMessage());
				break;
			case WARN:
				log.warn(input.getMessage());
				logger.warn(input.getMessage(), null);
				break;
			case ERROR:
				log.error(input.getMessage());
				logger.error(input.getMessage(), null);
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
