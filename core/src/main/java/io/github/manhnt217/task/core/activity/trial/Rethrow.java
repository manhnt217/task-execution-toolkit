package io.github.manhnt217.task.core.activity.trial;

import io.github.manhnt217.task.core.type.ObjectRef;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Rethrow {
    private String message;
    private ObjectRef<Exception> ex;
}
