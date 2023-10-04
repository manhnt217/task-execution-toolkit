package io.github.manhnt217.task.task_executor.process;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class TemplateExecutionException extends Exception {

    private final String templateName;

    public TemplateExecutionException(String templateName, JsonNode input, String message, Throwable e) {
        super(buildMessage(templateName, input, message), e);
        this.templateName = templateName;
    }

    private static String buildMessage(String templateName, JsonNode input, String message) {
        StringBuilder msg = new StringBuilder("Could not execute template '" + templateName + "'.");
        if (StringUtils.isNotBlank(message)) {
            msg.append(" Because " + message + ".");
        }
        if (input != null) {
            msg.append(" Input = " + input);
        }

        return msg.toString();
    }

    public TemplateExecutionException(String templateName, String message) {
        this(templateName, null, message, null);
    }

    public TemplateExecutionException(String templateName, JsonNode input, Throwable e) {
        this(templateName, input, null, e);
    }
}
