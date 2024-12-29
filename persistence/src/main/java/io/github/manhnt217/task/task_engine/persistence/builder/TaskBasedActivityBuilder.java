package io.github.manhnt217.task.task_engine.persistence.builder;

import io.github.manhnt217.task.task_engine.activity.task.TaskBasedActivity;
import io.github.manhnt217.task.task_engine.task.Task;

/**
 * @author manhnguyen
 */
public class TaskBasedActivityBuilder extends AbstractActivityBuilder<TaskBasedActivity, TaskBasedActivityBuilder>{
    private Task task;

    TaskBasedActivityBuilder(String name, Task task) {
        this.name = name;
        this.task = task;
    }

    @Override
    public TaskBasedActivity build() {
        TaskBasedActivity taskBasedActivity = new TaskBasedActivity(name, task);
        taskBasedActivity.setInputMapping(inputMapping);
        return taskBasedActivity;
    }
}
