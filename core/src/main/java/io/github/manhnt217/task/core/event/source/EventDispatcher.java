package io.github.manhnt217.task.core.event.source;

import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.container.exception.ContainerException;
import io.github.manhnt217.task.core.task.TaskException;

/**
 * @author manh nguyen
 */
public interface EventDispatcher {
    <E, R> R dispatch(EventSource<?, R> source, E event, Class<? extends R> returnType) throws ContainerException, TaskException, ActivityException;
}
