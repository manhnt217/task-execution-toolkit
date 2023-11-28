package io.github.manhnt217.task.core.container;

import io.github.manhnt217.task.core.task.event.EventSourceConfig;

/**
 * @author manhnguyen
 */
public interface EventSourceController {
    void deploy(EventSourceConfig eventSourceConfig);
    void undeploy(String eventSourceName) throws Exception;

    void startEventSource(String name);
    void stopEventSource(String name);
}
