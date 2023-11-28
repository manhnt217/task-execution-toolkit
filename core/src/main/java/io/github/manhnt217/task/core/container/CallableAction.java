package io.github.manhnt217.task.core.container;

/**
 * @author manhnguyen
 */
public interface CallableAction<R, E extends Throwable> {
    R call() throws E;
}
