package io.github.manhnt217.task.core.type;

public interface Future<T> {

    T get() throws Exception;

    T get(long timeout) throws Exception;
}
