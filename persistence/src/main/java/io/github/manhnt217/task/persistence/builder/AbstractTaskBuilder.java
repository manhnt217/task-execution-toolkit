package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.task.Task;

/**
 * @author manhnguyen
 */
public abstract class AbstractTaskBuilder<T extends Task> {

    protected String name;

    public abstract T build() throws ConfigurationException;
}
