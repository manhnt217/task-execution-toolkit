package io.github.manhnt217.task.core.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import io.github.manhnt217.task.core.exception.ExecutionException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author manh nguyen
 */
@Getter
public class TaskException extends ExecutionException {

    private final String taskName;

    public TaskException(String taskName, JsonNode input, String message, Exception e) {
        super(buildMessage(taskName, input, message), e);
        this.taskName = taskName;
    }

    public TaskException(String taskName, String message) {
        this(taskName, null, message, null);
    }

    private static String buildMessage(String taskName, JsonNode input, String message) {
        StringBuilder msg = new StringBuilder("Could not execute task '" + taskName + "'.");
        if (StringUtils.isNotBlank(message)) {
            msg.append(" Because " + message + ".");
        }
        if (input != null && !(input instanceof NullNode)) {
            msg.append(" Input = " + input);
        }

        return msg.toString();
    }
}
