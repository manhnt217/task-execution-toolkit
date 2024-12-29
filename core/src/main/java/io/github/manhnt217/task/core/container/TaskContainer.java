package io.github.manhnt217.task.core.container;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.core.activity.DefaultTaskLogger;
import io.github.manhnt217.task.core.container.exception.ContainerException;
import io.github.manhnt217.task.core.container.exception.EventSourceNotReadyException;
import io.github.manhnt217.task.core.container.exception.MultipleHandlersException;
import io.github.manhnt217.task.core.container.exception.NoHandlerException;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.inner.TransformException;
import io.github.manhnt217.task.core.repo.EngineRepository;
import io.github.manhnt217.task.core.task.RootContext;
import io.github.manhnt217.task.core.task.TaskException;
import io.github.manhnt217.task.core.task.event.EventSourceConfig;
import io.github.manhnt217.task.core.task.handler.Handler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.github.manhnt217.task.core.container.TaskContainer.EventSourceStatus.STARTED;
import static io.github.manhnt217.task.core.container.TaskContainer.EventSourceStatus.STOPPED;

@Slf4j
public class TaskContainer implements EventDispatcher, EventSourceController {

    private final ObjectNode globalProps;
    private final EngineRepository repo;
    private final ConcurrentMap<String, EventSourceRef> eventSources;
    private final ExecutorService executorService;
    private final SyncHelper syncHelper;

    public TaskContainer(ObjectNode globalProps, EngineRepository repo) {
        this.globalProps = globalProps;
        this.repo = repo;
        eventSources = new ConcurrentHashMap<>();
        executorService = Executors.newFixedThreadPool(100);
        syncHelper = new SyncHelper();
    }

    public void start() {
        deployAllEventSources();
    }

    public void shutdown() {
        shutdownAllEventSources();
    }

    private void deployAllEventSources() {
        List<EventSourceConfig> eventSourceConfigs = repo.findAllEventSources();
        for (EventSourceConfig eventSourceConfig : eventSourceConfigs) {
            try {
                deploy(eventSourceConfig);
            } catch (ContainerException e) {
                log.error("Exception while deploying event source '" + eventSourceConfig.getName() + "'", e);
            }
        }
    }

    private void shutdownAllEventSources() {
        for (EventSourceRef ref : eventSources.values()) {
            try {
                ref.getEventSource().shutdown();
            } catch (Exception e) {
                log.error("Exception while shutting down event source '" + ref.getEventSource().getName() + "'");
            } finally {
                ref.setStatus(STOPPED);
            }
        }
    }

    @Override
    public void deploy(EventSourceConfig eventSourceConfig) throws ContainerException {
        String sourceName = eventSourceConfig.getName();
        syncHelper.doSync(sourceName, () -> {
            if (eventSources.containsKey(sourceName)) {
                throw new IllegalStateException("EventSource '" + sourceName + "' has already been deployed. Undeploy first");
            }
            EventSource<?, ?, ?> eventSource = createEventSourceInstance(eventSourceConfig);
            eventSources.put(sourceName, new EventSourceRef(eventSource, STOPPED));

            if (eventSourceConfig.isAutoStart()) {
                startEventSource(sourceName, false);
            }
        });
    }

    private EventSource<?, ?, ?> createEventSourceInstance(EventSourceConfig eventSourceConfig) throws ContainerException {
        String propsJSLT = eventSourceConfig.getPropsJSLT();
        JsonNode pluginProps;
        try {
            pluginProps = JSONUtil.applyTransform(propsJSLT, globalProps);
        } catch (TransformException e) {
            throw new ContainerException("Error while transfrom input for eventsource. " +
                    "EventSource = '" + eventSourceConfig.getName() + "'. InputMapping = '" + propsJSLT + "'");
        }
        try {
            return (EventSource<?, ?, ?>) eventSourceConfig.createEventSource(this, pluginProps);
        } catch (Exception e) {
            throw new ContainerException("Exception while createing new event source instance. " +
                    "EventSource = '" + eventSourceConfig.getName() + "'", e);
        }
    }

    @Override
    public void undeploy(String name, boolean forceUndeploy) throws ContainerException {
        syncHelper.doSync(name, () -> {
            try {
                EventSourceRef ref = findEventSource(name);
                if (ref.getStatus() == STARTED) {
                    stopEventSource(name, false);
                }
                eventSources.remove(name);
            } catch (ContainerException e) {
                throw new RuntimeException(e);
            } finally {
                if (forceUndeploy) eventSources.remove(name);
            }
        });
    }

    @Override
    public void startEventSource(String name, boolean forceStart) throws ContainerException {
        syncHelper.doSync(name, () -> {
            EventSourceRef ref = findEventSource(name);
            if (ref.getStatus() == STARTED && !forceStart) {
                throw new ContainerException("Event source '" + name + "' has already started");
            }
            try {
                ref.getEventSource().start();
                ref.setStatus(STARTED);
            } catch (Exception e) {
                throw new ContainerException("Exception while starting event source '" + name + "'", e);
            }
        });
    }

    @Override
    public void stopEventSource(String name, boolean forceStop) throws ContainerException {
        syncHelper.doSync(name, () -> {
            EventSourceRef ref = findEventSource(name);
            if (ref.getStatus() == STOPPED && !forceStop) {
                throw new ContainerException("Event source '" + name + "' has already stopped");
            }
            try {
                ref.getEventSource().shutdown();
                ref.setStatus(STOPPED);
            } catch (Exception e) {
                throw new ContainerException("Exception while stopping event source '" + name + "'", e);
            }
        });
    }

    private EventSourceRef findEventSource(String name) throws ContainerException {
        EventSourceRef ref = eventSources.get(name);
        if (ref == null) {
            throw new ContainerException("Event source '" + name + "' has not been deployed yet");
        }
        return ref;
    }

    @Override
    public <E, R> R dispatch(EventSource<?, E, R> source, E event, Class<? extends E> eventType, Class<? extends R> returnType) throws ContainerException, TaskException, ActivityException {
        String sourceName = source.getName();
        String message = checkEventSourceBeforeDispatching(sourceName);
        if (StringUtils.isNotBlank(message)) {
            log.warn(message);
            throw new EventSourceNotReadyException(message);
        }
        List<Handler<E, R>> handlers = repo.findHandler(sourceName, eventType, returnType);
        if (handlers == null || handlers.isEmpty()) {
            throw new NoHandlerException(source);
        }
        if (source.isAsync()) {
            for (Handler<E, R> handler : handlers) {
                executorService.submit(() -> this.handle(handler, event));
            }
            return null;
        } else if (handlers.size() == 1) {
            return this.handle(handlers.get(0), event);
        } else {
            throw new MultipleHandlersException(source);
        }
    }

    @Override
    public <E, R> void shutdownEventSource(EventSource<?, E, R> source, boolean forceStop) throws ContainerException {
        this.stopEventSource(source.getName(), forceStop);
    }

    private String checkEventSourceBeforeDispatching(String sourceName) {
        return syncHelper.doSync(sourceName, () -> {
            EventSourceRef ref = eventSources.get(sourceName);
            if (ref == null) {
                return "EventSource '" + sourceName + "' has not been deployed yet";
            } else if (ref.getStatus() != STARTED) {
                return "EventSource '" + sourceName + "' has not been started yet";
            }
            return null;
        });
    }

    private <E, R> R handle(Handler<E, R> handler, E event) throws TaskException, ActivityException {
        RootContext context = new RootContext(globalProps, repo, new DefaultTaskLogger());
        return handler.handle(event, context);
    }

    enum EventSourceStatus {
        STARTED,
        STOPPED,
    }

    private static class EventSourceRef {
        @Getter
        private final EventSource<?, ?, ?> eventSource;
        @Getter
        @Setter
        private EventSourceStatus status;

        EventSourceRef(EventSource<?, ?, ?> eventSource, EventSourceStatus status) {
            this.eventSource = eventSource;
            this.status = status;
        }
    }
}
