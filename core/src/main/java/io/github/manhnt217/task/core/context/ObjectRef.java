package io.github.manhnt217.task.core.context;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author manh nguyen
 */
public class ObjectRef {

    @JsonIgnore
    private final Object value;

    public ObjectRef(Object value) {
        this.value = value;
    }

    @JsonIgnore
    public <T> T get() {
        return (T) this.value;
    }
}
