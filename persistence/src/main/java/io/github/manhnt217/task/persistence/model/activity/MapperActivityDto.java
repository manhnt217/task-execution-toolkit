package io.github.manhnt217.task.persistence.model.activity;

import io.github.manhnt217.task.persistence.model.ActivityGroupDto;

/**
 * @author manhnguyen
 */
public class MapperActivityDto extends ActivityDto {
    @Override
    public void setGroup(ActivityGroupDto group) {
        throw new UnsupportedOperationException("Mapper does not have group");
    }

    @Override
    public ActivityGroupDto getGroup() {
        throw new UnsupportedOperationException("Mapper does not have group");
    }
}
