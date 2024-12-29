package io.github.manhnt217.task.task_engine.task;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_engine.activity.ActivityLogger;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.exception.TaskException;

/**
 * @author manhnguyen
 */
public interface Task {

    String getName();

    JsonNode run(JsonNode input, String activityName, ActivityLogger activityLogger, ActivityContext context) throws TaskException;
}
