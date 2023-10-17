package io.github.manhnt217.task.task_executor.activity.impl;

import io.github.manhnt217.task.task_executor.common.CommonUtil;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * @author manhnguyen
 */
@Getter
public class ExecutionLog {

    private final String executionId;
    private final String activityName;
    private final OffsetDateTime logTime;
    private final String content;
    private final Severity severity;

    @Getter(AccessLevel.NONE)
    private final Throwable error;

    public ExecutionLog(String executionId, String activityName, Severity severity, String content, Throwable error) {
        this.executionId = executionId;
        this.activityName = activityName;
        this.error = error;
        this.logTime = OffsetDateTime.now(ZoneId.systemDefault());
        this.content = content;
        this.severity = severity;
    }

    public String getContent() {
        String errorSummary = CommonUtil.getErrorSummary(error);
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