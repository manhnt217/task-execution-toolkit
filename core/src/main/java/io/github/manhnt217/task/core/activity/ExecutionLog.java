package io.github.manhnt217.task.core.activity;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author manh nguyen
 */
@Getter
public class ExecutionLog {

    private final String executionId;
    private final String taskName;
    private final String activityName;
    private final OffsetDateTime logTime;
    private final String content;
    private final Severity severity;

    @Getter(AccessLevel.NONE)
    private final Throwable error;

    public ExecutionLog(String executionId, String taskName, String activityName, Severity severity, String content, Throwable error) {
        this.executionId = executionId;
        this.taskName = taskName;
        this.activityName = activityName;
        this.error = error;
        this.logTime = OffsetDateTime.now(ZoneId.systemDefault());
        this.content = content;
        this.severity = severity;
    }

    public static String getErrorSummary(Throwable err) {
        return ExceptionUtils.getThrowableList(err).stream()
                .map(t -> getShortClassName(t.getClass()) + ": " + StringUtils.defaultIfBlank(t.getMessage(), "").trim())
                .collect(Collectors.joining("\nCaused by: "));
    }

    private static String getShortClassName(Class<? extends Throwable> clazz) {
        return Arrays.stream(clazz.getPackage().getName().split("\\."))
                .map(t -> t.substring(0, 1)).collect(Collectors.joining(".")) + clazz.getName().substring(clazz.getName().lastIndexOf('.'));
    }

    public String getContent() {
        String errorSummary = getErrorSummary(error);
        if (StringUtils.isNotBlank(errorSummary)) {
            return content + " Error summary: " + errorSummary;
        } else {
            return content;
        }
    }

    public enum Severity {
        INFO, WARN, ERROR
    }
}
