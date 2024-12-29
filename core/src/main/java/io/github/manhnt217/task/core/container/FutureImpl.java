package io.github.manhnt217.task.core.container;

import io.github.manhnt217.task.core.exception.TimeOutException;
import io.github.manhnt217.task.core.type.Future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureImpl<T> implements Future<T> {

    private final java.util.concurrent.Future<? extends T> innerFuture;
    public FutureImpl(java.util.concurrent.Future<? extends T> innerFuture) {

        this.innerFuture = innerFuture;
    }

    @Override
    public T get() {
        try {
            return innerFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T get(long timeout) throws TimeOutException {
        try {
            return innerFuture.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new TimeOutException();
        }
    }
}
