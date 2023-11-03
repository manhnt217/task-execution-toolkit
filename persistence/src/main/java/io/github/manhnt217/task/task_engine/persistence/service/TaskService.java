package io.github.manhnt217.task.task_engine.persistence.service;

import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import io.github.manhnt217.task.task_engine.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.task_engine.persistence.builder.CompositeTaskBuilder;
import io.github.manhnt217.task.task_engine.persistence.model.TaskDto;
import io.github.manhnt217.task.task_engine.task.Task;

/**
 * @author manhnguyen
 */
public class TaskService {
    public static Task buildTask(TaskDto taskDto) throws ConfigurationException {
        switch (taskDto.getType()) {
            case PLUGIN:
                return buildPluginTask(taskDto);
            case COMPOSITE:
                return buildCompositeTask(taskDto);
            default:
                throw new IllegalArgumentException("Invalid task type: " + taskDto.getType());
        }
    }

    private static Task buildPluginTask(TaskDto taskDto) {
        return ActivityBuilder
                .plugin(taskDto.getName())
                .build();
    }

    private static Task buildCompositeTask(TaskDto taskDto) throws ConfigurationException {
        CompositeTaskBuilder compositeTaskBuilder = ActivityBuilder
                .composite(taskDto.getName())
                .outputMapping(taskDto.getOutputMapping());

        ActivityService.buildGroup(compositeTaskBuilder, taskDto.getGroup());

        return compositeTaskBuilder.build();
    }
}
