package io.github.manhnt217.task.sample.plugin;

import io.github.manhnt217.task.task_engine.activity.ExecutionLog;
import io.github.manhnt217.task.task_engine.task.PluginTask;
import io.github.manhnt217.task.task_engine.task.TaskLogger;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author manhnguyen
 */
@Slf4j
public class LogTask extends PluginTask<LogTask.LogDto, Object> {

    public LogTask(String name) {
        super(name);
    }

    @Override
    protected Class<? extends LogDto> getInputClass() {
        return LogDto.class;
    }

    @Override
    public Object exec(LogDto input, TaskLogger taskLogger) {
        switch (input.getSeverity()) {
            case INFO:
                log.info(input.getMessage());
                taskLogger.info(input.getMessage());
                break;
            case WARN:
                log.warn(input.getMessage());
                taskLogger.warn(input.getMessage(), null);
                break;
            case ERROR:
                log.error(input.getMessage());
                taskLogger.error(input.getMessage(), null);
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
