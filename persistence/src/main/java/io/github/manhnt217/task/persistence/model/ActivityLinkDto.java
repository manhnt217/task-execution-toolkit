package io.github.manhnt217.task.persistence.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public class ActivityLinkDto {
    private String from;
    private String to;
    private String guard;
}
