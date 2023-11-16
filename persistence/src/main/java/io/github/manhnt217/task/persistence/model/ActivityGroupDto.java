package io.github.manhnt217.task.persistence.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public class ActivityGroupDto {

    private List<ActivityDto> activities;

    private List<ActivityLinkDto> links;
}
