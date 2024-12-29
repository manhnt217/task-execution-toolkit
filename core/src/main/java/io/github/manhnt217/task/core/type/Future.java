package io.github.manhnt217.task.core.type;

import io.github.manhnt217.task.core.exception.CancelException;
import io.github.manhnt217.task.core.exception.TimeoutException;

public interface Future<T> {

    T get() throws CancelException;

    T get(long timeout) throws TimeoutException, CancelException;

    boolean isDone();
}
