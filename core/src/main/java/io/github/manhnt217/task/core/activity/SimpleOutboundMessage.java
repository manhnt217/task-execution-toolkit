package io.github.manhnt217.task.core.activity;

import com.fasterxml.jackson.databind.JsonNode;

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
