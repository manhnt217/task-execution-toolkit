package io.github.manhnt217.task.task_engine.type.simple;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.manhnt217.task.task_engine.type.EngineType;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
public final class ObjectRef implements EngineSimpleType {

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
