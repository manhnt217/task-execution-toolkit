package io.github.manhnt217.task.core.exception;

/**
 * Top-level class for all exceptions in this library.
 * Make it easier to distinguish between library's exceptions and outside world's exception
 *
 * @author manh nguyen
 */
public abstract class EngineException extends Exception {

    public EngineException(String message) {
        super(message);
    }

    public EngineException(String message, Throwable cause) {
        super(message, cause);
    }

    public Throwable getRootCause() {
        Throwable cause = getCause();
        if (cause instanceof EngineException) {
            return ((EngineException) cause).getRootCause();
        } else if (cause == null) {
            return this;
        } else {
            return cause;
        }
    }
}
