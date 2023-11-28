package io.github.manhnt217.task.core.repo;

import io.github.manhnt217.task.core.task.event.EventSourceConfig;

import java.util.List;

/**
 * @author manh nguyen
 */
public interface EventSourceResolver {
    EventSourceConfig resolveEventSource(String name);

    List<EventSourceConfig> findAllEventSources();
}
