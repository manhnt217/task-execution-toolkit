package io.github.manhnt217.task.core.type;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author manh nguyen
 */
public class ObjectRef<T> {

    @JsonIgnore
    private final T value;

    public ObjectRef(T value) {
        this.value = value;
    }

    @JsonIgnore
    public T get() {
        return this.value;
    }
}
