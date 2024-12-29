package io.github.manhnt217.task.task_engine.persistence.builder;

import io.github.manhnt217.task.task_engine.activity.task.TaskBasedActivity;
import io.github.manhnt217.task.task_engine.task.Task;

/**
 * @author manhnguyen
 */
public class TaskBasedActivityBuilder extends AbstractActivityBuilder<TaskBasedActivity, TaskBasedActivityBuilder>{

    private String taskName;

    TaskBasedActivityBuilder(String name) {
        this.name = name;
    }

    public TaskBasedActivityBuilder taskName(String taskName) {
        this.taskName = taskName;
        return this;
    }

    @Override
    public TaskBasedActivity build() {
        TaskBasedActivity taskBasedActivity = new TaskBasedActivity(name, taskName);
        taskBasedActivity.setInputMapping(inputMapping);
        return taskBasedActivity;
    }
}
