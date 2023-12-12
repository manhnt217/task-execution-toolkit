package io.github.manhnt217.task.core.container;

import io.github.manhnt217.task.core.container.exception.ContainerException;
import io.github.manhnt217.task.core.task.event.EventSourceConfig;

/**
 * @author manhnguyen
 */
public interface EventSourceController {
    void deploy(EventSourceConfig eventSourceConfig) throws ContainerException;
    void undeploy(String eventSourceName, boolean forceUndeploy) throws ContainerException;

    void startEventSource(String name, boolean forceStart) throws ContainerException;
    void stopEventSource(String name, boolean forceStop) throws ContainerException;
}
