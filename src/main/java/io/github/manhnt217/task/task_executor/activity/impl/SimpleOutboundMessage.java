package io.github.manhnt217.task.task_executor.activity.impl;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.activity.OutboundMessage;

/**
 * @author manhnguyen
 */
public class SimpleOutboundMessage implements OutboundMessage {

    private final JsonNode output;

    public SimpleOutboundMessage(JsonNode output) {
        this.output = output;
    }

    public static SimpleOutboundMessage of(JsonNode output) {
        return new SimpleOutboundMessage(output);
    }

    @Override
    public JsonNode getContent() {
        return output;
    }
}
