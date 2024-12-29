package io.github.manhnt217.task.core.container;

/**
 * @author manhnguyen
 */
public interface Action<E extends Throwable> {
    void act() throws E;
}
