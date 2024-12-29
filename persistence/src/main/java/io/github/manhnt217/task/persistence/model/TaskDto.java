package io.github.manhnt217.task.persistence.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public class TaskDto {

    private String name;

    private Type type;

    private String outputMapping;

    private ActivityGroupDto group;

    public enum Type {
        PLUGIN, COMPOSITE
    }
}
