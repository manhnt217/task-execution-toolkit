package io.github.manhnt217.task.core.container;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.manhnt217.task.core.activity.DefaultTaskLogger;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.event.source.EventDispatcher;
import io.github.manhnt217.task.core.event.source.EventSource;
import io.github.manhnt217.task.core.exception.MultipleHandlersException;
import io.github.manhnt217.task.core.exception.NoHandlerException;
import io.github.manhnt217.task.core.exception.TaskException;
import io.github.manhnt217.task.core.exception.inner.TransformException;
import io.github.manhnt217.task.core.repo.EngineRepository;
import io.github.manhnt217.task.core.task.TaskContext;
import io.github.manhnt217.task.core.task.event.EventSourceConfig;
import io.github.manhnt217.task.core.task.handler.Handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskContainer implements EventDispatcher, EventSourceController {

    private final ObjectNode globalProps;
    private final EngineRepository repo;
    private final Map<String, EventSource<?, ?>> eventSources;
    private final ExecutorService executorService;

    public TaskContainer(ObjectNode globalProps, EngineRepository repo) {
        this.globalProps = globalProps;
        this.repo = repo;
        eventSources = new HashMap<>();
        executorService = Executors.newFixedThreadPool(100);
    }

    public void start() {
        setUpEventSources();
    }

    private void setUpEventSources() {
        List<EventSourceConfig> eventSourceConfigs = repo.findAllEventSources();
        for (EventSourceConfig eventSourceConfig : eventSourceConfigs) {
            deploy(eventSourceConfig);
        }
    }

    @Override
    public void deploy(EventSourceConfig eventSourceConfig) {
        if (eventSources.containsKey(eventSourceConfig.getName())) {
            throw new IllegalStateException("EventSource '" + eventSourceConfig.getName() + "' has already been deployed. Undeploy first");
        }
        String propsJSLT = eventSourceConfig.getPropsJSLT();
        JsonNode pluginProps;
        try {
            pluginProps = JSONUtil.applyTransform(propsJSLT, globalProps);
        } catch (TransformException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }
        EventSource<?, ?> eventSource = eventSourceConfig.createEventSource(this, pluginProps);
        eventSources.put(eventSourceConfig.getName(), eventSource);

        if (eventSourceConfig.isAutoStart()) {
            startEventSource(eventSourceConfig.getName());
        }
    }

    @Override
    public void undeploy(String eventSourceName) throws Exception {
        EventSource<?, ?> ev = eventSources.get(eventSourceName);
        if (ev == null) {
            throw new IllegalStateException("EventSource '" + eventSourceName + "' has not been deployed");
        }
        ev.shutdown();
        eventSources.remove(eventSourceName);
    }

    @Override
    public void startEventSource(String name) {
        EventSource<?, ?> eventSource = eventSources.get(name);
        if (eventSource == null) {
            throw new IllegalArgumentException("Cannot find event source '" + name + "'");
        }

        try {
            eventSource.start();
        } catch (Exception e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stopEventSource(String name) {
        EventSource<?, ?> eventSource = eventSources.get(name);
        if (eventSource == null) {
            throw new IllegalArgumentException("Cannot find event source '" + name + "'");
        }

        try {
            eventSource.shutdown();
        } catch (Exception e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        try {
            for (EventSource<?, ?> eventSource : eventSources.values()) {
                eventSource.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E, R> R dispatch(EventSource<?, R> source, E event, Class<? extends R> returnType) throws NoHandlerException, MultipleHandlersException, TaskException {
        List<Handler> handlers = repo.findHandlerBySourceName(source.getName());
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
}
