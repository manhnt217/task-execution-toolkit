package io.github.manhnt217.task.core.context;

/**
 * @author manhnguyen
 */
public class Callstack {
    private final String top;
    private final Callstack tail;

    private Callstack(String top, Callstack tail) {
        this.top = top;
        this.tail = tail;
    }

    public String getTop() {
        return top;
    }

    private boolean isRoot() {
        return this.top == null;
    }

    public static Callstack root() {
        return new Callstack(null, null);
    }

    public static Callstack push(String name, Callstack tail) {
        return new Callstack(name, tail);
    }
}
