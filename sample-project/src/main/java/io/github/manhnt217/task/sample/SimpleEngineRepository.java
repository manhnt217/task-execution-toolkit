package io.github.manhnt217.task.sample;

import io.github.manhnt217.task.core.ClassUtil;
import io.github.manhnt217.task.core.event.source.EventSource;
import io.github.manhnt217.task.core.repo.EngineRepository;
import io.github.manhnt217.task.core.task.event.EventSourceConfig;
import io.github.manhnt217.task.core.task.function.Function;
import io.github.manhnt217.task.core.task.handler.Handler;
import io.github.manhnt217.task.core.task.plugin.Plugin;
import io.github.manhnt217.task.persistence.model.PluginDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author manh nguyen
 */
public class SimpleEngineRepository implements EngineRepository {

    protected final Map<String, Function> functions;
    protected final Map<String, Handler> handlers;
    protected final Map<String, Class<? extends Plugin>> functionPluginClasses;
    protected final Map<String, EventSourceConfig> eventSources;

    public SimpleEngineRepository() {
        functions = new HashMap<>();
        handlers = new HashMap<>();
        functionPluginClasses = new HashMap<>();
        eventSources = new HashMap<>();
    }

    public void registerFunction(Function function) {
        functions.put(function.getName(), function);
    }

    public void registerHandler(Handler handler) {
        handlers.put(handler.getName(), handler);
    }

    public void registerFunctionPlugin(PluginDto pluginDto) {
        functionPluginClasses.put(pluginDto.getName(), ClassUtil.findPlugin(pluginDto.getClassName(), Plugin.class));
    }

    public void registerEventSource(EventSourceConfig eventSourceConfig) {
        eventSourceConfig.loadClass();
        eventSources.put(eventSourceConfig.getName(), eventSourceConfig);
    }

    @Override
    public Function getFunction(String name) {
        return functions.get(name);
    }

    @Override
    public Class<? extends Plugin> resolveFunctionPluginClass(String pluginName) {
        return functionPluginClasses.get(pluginName);
    }

    @Override
    public EventSourceConfig resolveEventSource(String name) {
        return eventSources.get(name);
    }

    @Override
    public List<EventSourceConfig> findAllEventSources() {
        return eventSources.values().stream().collect(Collectors.toList());
    }

    @Override
    public Handler getHandler(String name) {
        return handlers.get(name);
    }

    @Override
    public List<Handler> findHandlerBySourceName(String sourceName) {
        return handlers.values().stream()
                .filter(h -> sourceName.equals(h.getSourceName()))
                .collect(Collectors.toList());
    }
}
