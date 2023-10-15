package io.github.manhnt217.task.task_executor.activity.impl.task.context;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.activity.Activity;
import io.github.manhnt217.task.task_executor.activity.OutboundMessage;
import io.github.manhnt217.task.task_executor.activity.impl.task.Task;

public interface ExecContext {
    void saveOutput(Activity activity, OutboundMessage output);

    JsonNode transformInput(Task task);

    JsonNode transform(String jslt);

    ExecContext createChild();

    JsonNode getProps();
}
