package io.github.manhnt217.task.core.task.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.ClassUtil;
import io.github.manhnt217.task.core.event.source.EventDispatcher;
import io.github.manhnt217.task.core.event.source.EventSource;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manh nguyen
 */
@Getter
@Setter
public class EventSourceConfig {
    private String name;
    private String pluginClassName;
    private boolean autoStart;
    private boolean async;
    private String propsJSLT;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private Class<? extends EventSource> pluginClass;

    public EventSource createEventSource(EventDispatcher dispatcher, JsonNode pluginProps) throws Exception {
        EventSource eventSource = ClassUtil.newPluginInstance(pluginClass);
        eventSource.setName(name);
        eventSource.setAsync(async);
        eventSource.setDispatcher(dispatcher);
        eventSource.setProps(pluginProps);
        return eventSource;
    }

    public void loadClass() {
        pluginClass = ClassUtil.findPlugin(pluginClassName, EventSource.class);
    }
}
