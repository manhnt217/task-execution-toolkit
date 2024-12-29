package io.github.manhnt217.task.core.container;

import io.github.manhnt217.task.core.type.Future;

import java.util.concurrent.TimeUnit;

public class FutureImpl<T> implements Future<T> {

    private final java.util.concurrent.Future<? extends T> innerFuture;
    public FutureImpl(java.util.concurrent.Future<? extends T> innerFuture) {

        this.innerFuture = innerFuture;
    }

    @Override
    public T get() throws Exception {
        return innerFuture.get();
    }

    @Override
    public T get(long timeout) throws Exception {
        return innerFuture.get(timeout, TimeUnit.MILLISECONDS);
    }
}
