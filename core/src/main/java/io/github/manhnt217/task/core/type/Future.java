package io.github.manhnt217.task.core.type;

import io.github.manhnt217.task.core.exception.TimeOutException;

public interface Future<T> {

    T get();

    T get(long timeout) throws TimeOutException;
}
