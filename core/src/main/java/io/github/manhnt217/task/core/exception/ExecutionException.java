package io.github.manhnt217.task.core.exception;

/**
 * Top-level class for all exceptions in this library.
 * Make it easier to distinguish between library's exceptions and outside world's exception
 *
 * @author manh nguyen
 */
public abstract class ExecutionException extends Exception {

    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public Throwable getRootCause() {
        Throwable cause = getCause();
        if (cause instanceof ExecutionException) {
            return ((ExecutionException) cause).getRootCause();
        } else if (cause == null) {
            return this;
        } else {
            return cause;
        }
    }

    public ActivityException getRootActivityException() {
        return getRootActivityException0(null);
    }

    protected final ActivityException getRootActivityException0(ActivityException currentActivityException) {
        ActivityException aE = this instanceof ActivityException ? ((ActivityException) this) : currentActivityException;
        Throwable cause = getCause();
        if (cause instanceof ExecutionException) {
            return ((ExecutionException) cause).getRootActivityException0(aE);
        } else {
            return aE;
        }
    }
}
