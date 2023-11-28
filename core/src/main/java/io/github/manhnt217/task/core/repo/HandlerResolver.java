package io.github.manhnt217.task.core.repo;

import io.github.manhnt217.task.core.task.handler.Handler;

import java.util.List;

/**
 * @author manhnguyen
 */
public interface HandlerResolver {
    public Handler getHandler(String name);
    public List<Handler> findHandlerBySourceName(String sourceName);
}
