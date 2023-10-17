package io.github.manhnt217.task.task_executor.activity;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author manhnguyen
 */
public interface OutboundMessage {

    JsonNode getContent();

    default boolean isEmpty() {
        return getContent() == null;
    }
}
