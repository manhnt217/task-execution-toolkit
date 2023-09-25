package io.github.manhnt217.task.task_executor.executor;

import io.github.manhnt217.task.task_executor.task.Task;

public class SubTaskExecutionException extends TaskExecutionException {
    public SubTaskExecutionException(Task task, TaskExecutionException e) {
        super("Cannot execute task because a failure in subtask '" + e.getTask().getId() + "'\nCaused by: " + e.getMessage(), task);
    }
}
