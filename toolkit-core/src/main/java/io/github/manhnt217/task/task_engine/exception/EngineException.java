package io.github.manhnt217.task.task_engine.exception;

/**
 * Top-level class for all exceptions in this library.
 * Make it easier to distinguish between library's exceptions and outside world's exception
 *
 * @author manhnguyen
 */
public abstract class EngineException extends Exception {

    public EngineException(String message) {
        super(message);
    }

    public EngineException(String message, Throwable cause) {
        super(message, cause);
    }

    public EngineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
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
