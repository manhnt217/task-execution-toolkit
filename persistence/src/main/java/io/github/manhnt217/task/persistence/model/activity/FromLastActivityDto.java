package io.github.manhnt217.task.persistence.model.activity;

import io.github.manhnt217.task.persistence.model.ActivityGroupDto;

/**
 * @author manhnguyen
 */
public class FromLastActivityDto extends ActivityDto {

    @Override
    public void setGroup(ActivityGroupDto group) {
        throw new UnsupportedOperationException("FromLast does not have group");
    }

    @Override
    public ActivityGroupDto getGroup() {
        throw new UnsupportedOperationException("FromLast does not have group");
    }

    @Override
    public void setInputMapping(String inputMapping) {
        throw new UnsupportedOperationException("Input mapping for FromLastActivityDto will be automatically calculated at runtime");
    }

    @Override
    public String getInputMapping() {
        throw new UnsupportedOperationException("Input mapping for FromLastActivityDto will be automatically calculated at runtime");
    }
}
