package io.github.manhnt217.task.core.exception;

/**
 * @author manhnguyen
 */
public class CancelException extends EngineException {

    public CancelException() {
        super("Wait timed out");
    }
}
