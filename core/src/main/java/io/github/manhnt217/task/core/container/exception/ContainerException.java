package io.github.manhnt217.task.core.container.exception;

/**
 * @author manhnguyen
 */
public class ContainerException extends Exception {

    public ContainerException(String message) {
        super(message);
    }

    public ContainerException(String message, Throwable cause) {
        super(message, cause);
    }
}
