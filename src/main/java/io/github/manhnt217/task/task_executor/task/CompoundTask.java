package io.github.manhnt217.task.task_executor.task;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CompoundTask extends Task {

	private Set<Task> subTasks;

	public CompoundTask(Set<Task> subTasks) {
		this.subTasks = subTasks;
	}
}
