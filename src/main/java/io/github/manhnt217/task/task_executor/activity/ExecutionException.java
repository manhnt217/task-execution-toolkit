package io.github.manhnt217.task.task_executor.activity;

/**
 * @author manhnguyen
 */
public class ExecutionException extends Exception {
    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException(Throwable cause) {
        super(cause);
    }
}
