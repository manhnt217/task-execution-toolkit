package io.github.manhnt217.task.core.event.sink;

/**
 * @author manh nguyen
 */
public interface EventSink<E, R> {

    String getName();
    boolean autoStart();
    void start() throws Exception;
    void stop() throws Exception;

    R consume(E event);
}
