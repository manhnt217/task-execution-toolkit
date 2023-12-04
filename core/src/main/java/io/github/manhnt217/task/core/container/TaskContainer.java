package io.github.manhnt217.task.core.container;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.core.activity.DefaultTaskLogger;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.event.source.EventDispatcher;
import io.github.manhnt217.task.core.event.source.EventSource;
import io.github.manhnt217.task.core.exception.*;
import io.github.manhnt217.task.core.exception.inner.TransformException;
import io.github.manhnt217.task.core.repo.EngineRepository;
import io.github.manhnt217.task.core.task.TaskContext;
import io.github.manhnt217.task.core.task.event.EventSourceConfig;
import io.github.manhnt217.task.core.task.handler.Handler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;
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
            EventSource<?, ?> eventSource = createEventSourceInstance(eventSourceConfig);
            eventSources.put(sourceName, new EventSourceRef(eventSource, STOPPED));

            if (eventSourceConfig.isAutoStart()) {
                startEventSource(sourceName, false);
            }
        });
    }

    private EventSource<?, ?> createEventSourceInstance(EventSourceConfig eventSourceConfig) throws ContainerException {
        String propsJSLT = eventSourceConfig.getPropsJSLT();
        JsonNode pluginProps;
        try {
            pluginProps = JSONUtil.applyTransform(propsJSLT, globalProps);
        } catch (TransformException e) {
            throw new ContainerException("Error while transfrom input for eventsource. " +
                    "EventSource = '" + eventSourceConfig.getName() + "'. InputMapping = '" + propsJSLT + "'");
        }
        try {
            return (EventSource<?, ?>) eventSourceConfig.createEventSource(this, pluginProps);
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
    public <E, R> R dispatch(EventSource<?, R> source, E event, Class<? extends R> returnType) throws ContainerException, TaskException {
        String sourceName = source.getName();
        String message = checkEventSourceBeforeDispatching(sourceName);
        if (StringUtils.isNotBlank(message)) {
            log.warn(message);
            throw new EventSourceNotReadyException(message);
        }
        List<Handler> handlers = repo.findHandlerBySourceName(sourceName);
        if (handlers == null || handlers.isEmpty()) {
            throw new NoHandlerException(source);
        }
        if (source.isAsync()) {
            for (Handler handler : handlers) {
                executorService.submit(() -> this.handle(handler, event, returnType));
            }
            return null;
        } else if (handlers.size() == 1) {
            return this.handle(handlers.get(0), event, returnType);
        } else {
            throw new MultipleHandlersException(source);
        }
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

    private <E, R> R handle(Handler handler, E event, Class<? extends R> returnType) throws TaskException {
        TaskContext context = new TaskContext(UUID.randomUUID().toString(), globalProps, repo, new DefaultTaskLogger());
        JsonNode input = JSONUtil.valueToTree(event, context);
        JsonNode output = handler.handle(input, context);
        try {
            return JSONUtil.treeToValue(output, returnType, context);
        } catch (JsonProcessingException e) {
            throw new TaskException(handler.getName(), "Exception while deserialize output");
        }
    }

    enum EventSourceStatus {
        STARTED,
        STOPPED,
    }

    private static class EventSourceRef {
        @Getter
        private final EventSource<?, ?> eventSource;
        @Getter
        @Setter
        private EventSourceStatus status;

        EventSourceRef(EventSource<?, ?> eventSource, EventSourceStatus status) {
            this.eventSource = eventSource;
            this.status = status;
        }
    }
}
