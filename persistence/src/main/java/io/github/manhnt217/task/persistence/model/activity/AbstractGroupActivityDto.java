package io.github.manhnt217.task.persistence.model.activity;

import io.github.manhnt217.task.persistence.model.ActivityGroupDto;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public abstract class AbstractGroupActivityDto extends ActivityDto {
    private String startName;
    private String endName;
    private String outputMapping;
    private ActivityGroupDto group;
}
