package io.github.manhnt217.task.core.container;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.container.exception.ContainerException;
import io.github.manhnt217.task.core.task.TaskException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class EventSource<P, E, R> {

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

    final synchronized void start() throws Exception {
        if (running) {
            return;
        }
        checkReady();
        P p =  JSONUtil.MAPPER.treeToValue(props, JSONUtil.MAPPER.constructType(getPropsType()));
        startInternal(p);
        running = true;
    }

    final synchronized void shutdown() throws Exception {
        if (!running) {
            return;
        }
        log.info("Shutting down event source '" + this.name + "'");
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
    protected final R dispatch(E e) throws ContainerException, TaskException, ActivityException {
        return dispatcher.dispatch(this, e, getDispatcherEventType(), getDispatcherReturnType());
    }

    protected abstract Class<? extends P> getPropsType();
    public abstract Class<? extends E> getDispatcherEventType();
    public abstract Class<? extends R> getDispatcherReturnType();
    protected abstract void startInternal(P props) throws Exception;
    protected abstract void shutdownInternal() throws Exception ;

    protected void shutdownSelf(boolean forceStop) throws ContainerException {
        dispatcher.shutdownEventSource(this, forceStop);
    }
}
