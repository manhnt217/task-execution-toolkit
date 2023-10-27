package io.github.manhnt217.task.task_engine.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
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
