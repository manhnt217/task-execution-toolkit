package io.github.manhnt217.task.task_engine.exception;

/**
 * @author manhnguyen
 */
public class GroupException extends EngineException {
    public GroupException(String message) {
        super(message);
    }

    public GroupException(String message, Throwable e) {
        super(message, e);
    }
}