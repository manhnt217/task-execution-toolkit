package io.github.manhnt217.task.task_executor.task;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.activity.Activity;
import io.github.manhnt217.task.task_executor.activity.OutboundMessage;

public interface ExecContext {
    void saveOutput(Activity activity, OutboundMessage output);

    void setParentInput(JsonNode globalParams);

    JsonNode transformInput(Task task);

    JsonNode transform(String jslt);
}
