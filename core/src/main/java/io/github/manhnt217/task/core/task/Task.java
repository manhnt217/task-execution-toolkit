package io.github.manhnt217.task.core.task;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.ActivityLogger;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.exception.TaskException;

/**
 * @author manhnguyen
 */
public interface Task {

    String getName();

    JsonNode run(JsonNode input, String activityName, ActivityLogger activityLogger, ActivityContext context) throws TaskException;
}
