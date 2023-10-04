package io.github.manhnt217.task.task_executor.executor;

import io.github.manhnt217.task.task_executor.task.Task;

public class SubTaskExecutionException extends TaskExecutionException {
    public SubTaskExecutionException(Task task, TaskExecutionException e) {
        super("An error occurred in subtask '" + e.getTask().getTaskName() + "'", task, e);
    }
}
