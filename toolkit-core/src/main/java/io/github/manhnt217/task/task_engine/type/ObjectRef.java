package io.github.manhnt217.task.task_engine.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
public class ObjectRef implements EngineType {

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
