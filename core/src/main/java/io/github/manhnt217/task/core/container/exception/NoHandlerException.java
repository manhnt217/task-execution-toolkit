package io.github.manhnt217.task.core.container.exception;

import io.github.manhnt217.task.core.container.EventSource;

/**
 * @author manhnguyen
 */
public class NoHandlerException extends ContainerException {

    public NoHandlerException(EventSource source) {
        super("There is no handler for event source '" + source.getName() + "'");
    }
}
