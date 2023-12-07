package io.github.manhnt217.task.core.container;

import io.github.manhnt217.task.core.type.Future;

import java.util.concurrent.Callable;

public interface FutureProcessor {
    <T> Future<T> submit(Callable<T> callable);

    boolean shutdown(long timeout, boolean force) throws Exception;
}
