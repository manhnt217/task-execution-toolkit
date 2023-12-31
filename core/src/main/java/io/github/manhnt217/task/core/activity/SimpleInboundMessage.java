package io.github.manhnt217.task.core.activity;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author manh nguyen
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
