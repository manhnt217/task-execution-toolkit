package io.github.manhnt217.task.task_engine.context;

import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public class ObjectRef {

    private final String refId;

    ObjectRef(String refId) {
        this.refId = refId;
    }
}
