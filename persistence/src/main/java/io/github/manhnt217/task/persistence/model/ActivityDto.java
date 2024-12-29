package io.github.manhnt217.task.persistence.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author manh nguyen
 */
@Getter
@Setter
public class ActivityDto {

    private String name;
    private String startName;
    private String endName;
    private String inputMapping;
    private String outputMapping;
    private String task;
    private String pluginName;
    private String sourceName;
    private Type type;

    private ActivityGroupDto group;

    public enum Type {
        GROUP, FOREACH, TRY, FUNC, PLUGIN, SOURCE
    }
}
