package io.github.manhnt217.task.task_executor.task;

import com.fasterxml.jackson.databind.JsonNode;

public interface Proc {
	JsonNode process(JsonNode inputJSON);
}
