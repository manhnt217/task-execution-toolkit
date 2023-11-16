package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.activity.task.TaskBasedActivity;

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
