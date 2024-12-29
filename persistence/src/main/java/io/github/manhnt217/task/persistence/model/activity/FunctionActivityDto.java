package io.github.manhnt217.task.persistence.model.activity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
@Getter @Setter
public class FunctionActivityDto extends ActivityDto {
    private String functionName;
}
