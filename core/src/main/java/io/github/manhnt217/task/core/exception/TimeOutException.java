package io.github.manhnt217.task.core.exception;

/**
 * @author manhnguyen
 */
public class TimeOutException extends ContainerException {

    public TimeOutException() {
        super("Wait timed out");
    }
}
