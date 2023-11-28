package io.github.manhnt217.task.sample.plugin;

import io.github.manhnt217.task.core.activity.ExecutionLog;
import io.github.manhnt217.task.core.task.plugin.Plugin;
import io.github.manhnt217.task.core.task.plugin.PluginLogger;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author manh nguyen
 */
@Slf4j
public class Log extends Plugin<Log.LogDto, Object> {

    @Override
    protected Class<? extends LogDto> getInputType() {
        return LogDto.class;
    }

    @Override
    public Object exec(LogDto input, PluginLogger logger) {
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
        private ExecutionLog.Severity severity;
        private String message;
    }
}
