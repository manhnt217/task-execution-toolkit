package io.github.manhnt217.task.core.container;

import io.github.manhnt217.task.core.type.Future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DefaultFutureProcessor implements FutureProcessor {

    private final ExecutorService executors;

    public DefaultFutureProcessor(int numOfThreads) {
        executors = Executors.newFixedThreadPool(numOfThreads);
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        java.util.concurrent.Future<T> innerFuture = executors.submit(callable);
        return new FutureImpl<>(innerFuture);
    }

    @Override
    public boolean shutdown(long timeout, boolean force) throws Exception {
        if (force) {
            executors.shutdownNow();
            return true;
        } else {
            executors.shutdown();
            return executors.awaitTermination(timeout, TimeUnit.MILLISECONDS);
        }
    }
}
