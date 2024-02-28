package io.github.manhnt217.task.persistence.model;

import io.github.manhnt217.task.persistence.model.activity.simple.SourceActivityDto;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public class HandlerDto {
    private String name;
    private String eventClass;
    private String outputClass;
    private String outputMapping;
    private SourceActivityDto fromSourceActivity;
    private ActivityGroupDto group;
}
