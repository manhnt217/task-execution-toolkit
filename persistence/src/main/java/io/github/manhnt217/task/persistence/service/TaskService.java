package io.github.manhnt217.task.persistence.service;

import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.persistence.builder.CompositeTaskBuilder;
import io.github.manhnt217.task.persistence.model.TaskDto;
import io.github.manhnt217.task.core.task.Task;

/**
 * @author manhnguyen
 */
public class TaskService {

    private static final TaskService INSTANCE = new TaskService();

    private final ActivityService activityService = ActivityService.instance();

    private TaskService() {

    }

    public static TaskService instance() {
        return INSTANCE;
    }

    public Task buildTask(TaskDto taskDto) throws ConfigurationException {
        switch (taskDto.getType()) {
            case PLUGIN:
                return buildPluginTask(taskDto);
            case COMPOSITE:
                return buildCompositeTask(taskDto);
            default:
                throw new IllegalArgumentException("Invalid task type: " + taskDto.getType());
        }
    }

    private Task buildPluginTask(TaskDto taskDto) {
        return ActivityBuilder
                .plugin(taskDto.getName())
                .build();
    }

    private Task buildCompositeTask(TaskDto taskDto) throws ConfigurationException {
        CompositeTaskBuilder compositeTaskBuilder = ActivityBuilder
                .composite(taskDto.getName())
                .outputMapping(taskDto.getOutputMapping());

        activityService.buildGroup(compositeTaskBuilder, taskDto.getGroup());

        return compositeTaskBuilder.build();
    }
}
