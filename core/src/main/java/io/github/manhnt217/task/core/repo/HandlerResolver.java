package io.github.manhnt217.task.core.repo;

import io.github.manhnt217.task.core.task.handler.Handler;

import java.util.List;

/**
 * @author manhnguyen
 */
public interface HandlerResolver {
    Handler getHandler(String name);
    <E, R> List<Handler<E, R>> findHandler(String sourceName, Class<? extends E> eventType, Class<? extends R> returnType);
}
