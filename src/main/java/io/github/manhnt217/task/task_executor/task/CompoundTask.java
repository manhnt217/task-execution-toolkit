package io.github.manhnt217.task.task_executor.task;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CompoundTask extends Task {

	private List<Task> subTasks;

	public CompoundTask() {
		this.subTasks = new ArrayList<>(0);
	}

	public CompoundTask(List<Task> subTasks) {
		this.subTasks = new ArrayList<>(subTasks);
	}
}
