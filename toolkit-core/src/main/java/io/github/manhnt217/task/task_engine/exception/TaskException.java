package io.github.manhnt217.task.task_engine.exception;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author manhnguyen
 */
@Getter
public class TaskException extends EngineException {

    private final String taskName;

    public TaskException(String taskName, JsonNode input, String message, Throwable e) {
        super(buildMessage(taskName, input, message), e);
        this.taskName = taskName;
    }

    private static String buildMessage(String taskName, JsonNode input, String message) {
        StringBuilder msg = new StringBuilder("Could not execute task '" + taskName + "'.");
        if (StringUtils.isNotBlank(message)) {
            msg.append(" Because " + message + ".");
        }
        if (input != null) {
            msg.append(" Input = " + input);
        }

        return msg.toString();
    }

    public TaskException(String taskName, String message) {
        this(taskName, null, message, null);
    }

    public TaskException(String taskName, JsonNode input, Throwable e) {
        this(taskName, input, null, e);
    }
}
