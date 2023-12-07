package io.github.manhnt217.task.core.type;

public class FutureRef<T> extends ObjectRef {
    public FutureRef(Future<T> value) {
        super(value);
    }
}
