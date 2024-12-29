package io.github.manhnt217.task.core.event.source;

import io.github.manhnt217.task.core.exception.ContainerException;
import io.github.manhnt217.task.core.exception.TaskException;

/**
 * @author manh nguyen
 */
public interface EventDispatcher {
    <E, R> R dispatch(EventSource<?, R> source, E event, Class<? extends R> returnType) throws ContainerException, TaskException;
}