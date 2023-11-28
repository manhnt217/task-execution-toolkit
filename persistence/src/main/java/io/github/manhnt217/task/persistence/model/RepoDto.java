package io.github.manhnt217.task.persistence.model;

import io.github.manhnt217.task.core.task.event.EventSourceConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author manh nguyen
 */
@Getter
@Setter
public class RepoDto {
    private List<PluginDto> plugins;
    private List<FunctionDto> functions;
    private List<EventSourceConfig> sources;
    private List<HandlerDto> handlers;
}
