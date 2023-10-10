package io.github.manhnt217.task.task_executor.activity;

import com.fasterxml.jackson.databind.JsonNode;

public interface InboundMessage {
    JsonNode getContent();
}
