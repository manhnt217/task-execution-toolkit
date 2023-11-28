package io.github.manhnt217.task.core.exception;

import io.github.manhnt217.task.core.event.source.EventSource;

/**
 * @author manhnguyen
 */
public class MultipleHandlersException extends EngineException {

    public MultipleHandlersException(EventSource source) {
        super("There are multiple handlers for event source '" + source.getName() + "' when broadcast mode is off");
    }
}
