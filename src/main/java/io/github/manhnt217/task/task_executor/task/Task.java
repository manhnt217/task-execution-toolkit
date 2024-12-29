package io.github.manhnt217.task.task_executor.task;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.activity.ActivityLogger;
import io.github.manhnt217.task.task_executor.context.ActivityContext;

/**
 * @author manhnguyen
 */
public interface Task {

    String getName();

    @SuppressWarnings("unused")
    JsonNode run(JsonNode input, String activityName, ActivityLogger activityLogger, ActivityContext context) throws TaskExecutionException;
}
