package io.github.manhnt217.task.persistence.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author manh nguyen
 */
@Getter
@Setter
public class FunctionDto {

    private String name;
    private String inputClass;
    private String outputClass;
    private String outputMapping;
    private ActivityGroupDto group;
    private Type type;

    public enum Type {
        FUNCTION, ROUTINE, PRODUCER, CONSUMER
    }
}
