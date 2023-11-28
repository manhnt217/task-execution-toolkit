package io.github.manhnt217.task.persistence.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public class HandlerDto {
    private String name;
    private String outputMapping;
    private ActivityDto fromSourceActivity;
    private ActivityGroupDto group;
}
