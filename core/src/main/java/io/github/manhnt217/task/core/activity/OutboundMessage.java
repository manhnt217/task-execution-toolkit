package io.github.manhnt217.task.core.activity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

/**
 * @author manh nguyen
 */
public interface OutboundMessage {

    JsonNode getContent();

    default boolean isEmpty() {
        return getContent() == null || getContent() instanceof NullNode;
    }
}
