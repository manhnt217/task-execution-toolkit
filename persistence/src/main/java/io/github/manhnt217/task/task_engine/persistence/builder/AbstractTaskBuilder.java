package io.github.manhnt217.task.task_engine.persistence.builder;

import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import io.github.manhnt217.task.task_engine.task.Task;

/**
 * @author manhnguyen
 */
public abstract class AbstractTaskBuilder<T extends Task, B extends AbstractTaskBuilder<T, B>> {

    protected String name;

    public abstract T build() throws ConfigurationException;
}
