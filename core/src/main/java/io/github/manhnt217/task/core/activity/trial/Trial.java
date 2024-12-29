package io.github.manhnt217.task.core.activity.trial;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.type.ObjectRef;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manh nguyen
 */
@Getter
@Setter
public class Trial {
    private JsonNode success;
    private ObjectRef<Throwable> failure;

    public Trial() {
    }

    private Trial(JsonNode success, ObjectRef<Throwable> failure) {
        this.success = success;
        this.failure = failure;
    }

    public static Trial success(JsonNode rs) {
        return new Trial(rs, null);
    }

    public static Trial failure(Throwable ex) {
        return new Trial(null, new ObjectRef<>(ex));
    }
}
