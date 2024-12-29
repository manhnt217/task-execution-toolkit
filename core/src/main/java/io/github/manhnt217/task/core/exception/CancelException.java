package io.github.manhnt217.task.core.exception;

/**
 * @author manhnguyen
 */
public class CancelException extends ExecutionException {

    public CancelException() {
        super("Wait timed out");
    }
}
