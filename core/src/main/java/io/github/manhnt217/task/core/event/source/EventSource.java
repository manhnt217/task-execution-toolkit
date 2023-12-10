package io.github.manhnt217.task.core.event.source;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.container.exception.ContainerException;
import io.github.manhnt217.task.core.task.TaskException;
import lombok.Getter;
import lombok.Setter;

public abstract class EventSource<P, R> {

    @Getter @Setter
    private String name;

    @Getter @Setter
    private boolean async;

    @Setter
    private EventDispatcher dispatcher;

    @Getter
    private volatile boolean running = false;

    @Setter
    private JsonNode props;

    public final synchronized void start() throws Exception {
        if (running) {
            return;
        }
        checkReady();
        P p =  JSONUtil.MAPPER.treeToValue(props, JSONUtil.MAPPER.constructType(getPropsType()));
        startInternal(p);
        running = true;
    }

    public synchronized final void shutdown() throws Exception {
        if (!running) {
            return;
        }
        shutdownInternal();
        running = false;
    }


    protected final void checkReady() {
        if (dispatcher == null) {
            throw new IllegalStateException("No event dispatcher was set");
        }
    }

    /**
     * This method should only be called after {@link #startInternal(Object)} has successfully returned.
     * That means it should not be called inside starting process of the source.
     * @param e the event object
     * @return <code>null</code> only when async = true
     */
    protected final R dispatch(Object e) throws ContainerException, TaskException, ActivityException {
        return dispatcher.dispatch(this, e, getDispatcherReturnType());
    }

    protected abstract Class<? extends P> getPropsType();
    public abstract Class<? extends R> getDispatcherReturnType();
    protected abstract void startInternal(P props) throws Exception;
    protected abstract void shutdownInternal() throws Exception ;
}
