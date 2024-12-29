package io.github.manhnt217.task.task_engine.persistence.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public class ActivityDto {

    private String name;
    private String startName;
    private String endName;
    private String inputMapping;
    private String outputMapping;
    private TaskDto task;
    private Type type;

    private ActivityGroupDto group;

    public enum Type {
        GROUP, FOREACH, TRY, TASK
    }
}
