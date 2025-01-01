package io.github.manhnt217.task.play_ground.dto;

import io.github.manhnt217.task.persistence.model.FunctionDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public class TaskExecutionReq {
	private FunctionDto taskDescription;
	private Map<?, ?> input;
}
