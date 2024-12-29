package io.github.manhnt217.task.core.exception;

/**
 * @author manhnguyen
 */
public class TimeoutException extends EngineException {

    public TimeoutException(long timeout) {
        super("Timed out after " + timeout + " milliseconds");
    }
}
