package io.github.manhnt217.task.core.container;

import io.github.manhnt217.task.core.container.exception.ContainerException;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.task.TaskException;

/**
 * @author manh nguyen
 */
public interface EventDispatcher {
    <E, R> R dispatch(EventSource<?, E, R> source, E event, Class<? extends E> eventType, Class<? extends R> returnType) throws ContainerException, TaskException, ActivityException;

    <E, R> void shutdownEventSource(EventSource<?, E, R> source, boolean forceStop) throws ContainerException;
}
