package io.github.manhnt217.task.task_engine.activity.impl;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_engine.activity.InboundMessage;

/**
 * @author manhnguyen
 */
public class SimpleInboundMessage implements InboundMessage {

    private final JsonNode input;

    public SimpleInboundMessage(JsonNode input) {
        this.input = input;
    }

    public static SimpleInboundMessage of(JsonNode input) {
        return new SimpleInboundMessage(input);
    }

    public static SimpleInboundMessage empty() {
        return new SimpleInboundMessage(null);
    }

    @Override
    public JsonNode getContent() {
        return input;
    }
}
