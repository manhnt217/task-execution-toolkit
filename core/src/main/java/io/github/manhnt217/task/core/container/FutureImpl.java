package io.github.manhnt217.task.core.container;

import io.github.manhnt217.task.core.exception.CancelException;
import io.github.manhnt217.task.core.exception.TimeoutException;
import io.github.manhnt217.task.core.type.Future;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class FutureImpl<T> implements Future<T> {

    private final java.util.concurrent.Future<? extends T> innerFuture;
    public FutureImpl(java.util.concurrent.Future<? extends T> innerFuture) {

        this.innerFuture = innerFuture;
    }

    @Override
    public T get() throws CancelException {
        try {
            return innerFuture.get();
        } catch (CancellationException e) {
            throw new CancelException();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T get(long timeout) throws TimeoutException, CancelException {
        try {
            return innerFuture.get(timeout, TimeUnit.MILLISECONDS);
        } catch (CancellationException e) {
            throw new CancelException();
        } catch (java.util.concurrent.TimeoutException e) {
            throw new TimeoutException(timeout);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isDone() {
        return innerFuture.isDone();
    }
}
