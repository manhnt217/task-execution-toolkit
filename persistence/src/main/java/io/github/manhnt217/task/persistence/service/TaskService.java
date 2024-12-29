package io.github.manhnt217.task.persistence.service;

import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.task.function.Function;
import io.github.manhnt217.task.core.task.handler.Handler;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.persistence.builder.FunctionBuilder;
import io.github.manhnt217.task.persistence.builder.HandlerBuilder;
import io.github.manhnt217.task.persistence.model.ActivityDto;
import io.github.manhnt217.task.persistence.model.FunctionDto;
import io.github.manhnt217.task.persistence.model.HandlerDto;

/**
 * @author manh nguyen
 */
public class TaskService {

    private static final TaskService INSTANCE = new TaskService();

    private final ActivityService activityService = ActivityService.instance();

    private TaskService() {

    }

    public static TaskService instance() {
        return INSTANCE;
    }

    public Function buildFunction(FunctionDto functionDto) throws ConfigurationException {
        FunctionBuilder functionBuilder;
        try {
            if (functionDto.getInputClass() == null) {
                if (functionDto.getOutputClass() == null) {
                    functionBuilder = ActivityBuilder
                            .routine(functionDto.getName());
                } else {
                    functionBuilder = ActivityBuilder
                            .producer(functionDto.getName(), Class.forName(functionDto.getOutputClass()));
                }
            } else {
                if (functionDto.getOutputClass() == null) {
                    functionBuilder = ActivityBuilder
                            .consumer(functionDto.getName(), Class.forName(functionDto.getInputClass()));
                } else {
                    functionBuilder = ActivityBuilder
                            .function(functionDto.getName(), Class.forName(functionDto.getInputClass()), Class.forName(functionDto.getOutputClass()));
                }
            }
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException("Input/Output class not found for function '" + functionDto.getName() + "'", e);
        }

        functionBuilder.outputMapping(functionDto.getOutputMapping());

        activityService.buildGroupBuilder(functionBuilder, functionDto.getGroup());

        return functionBuilder.build();
    }

    public Handler buildHandler(HandlerDto handlerDto) throws ConfigurationException {
        ActivityDto fromSourceActivity = handlerDto.getFromSourceActivity();
        if (fromSourceActivity.getType() != ActivityDto.Type.SOURCE) {
            throw new IllegalArgumentException("fromSourceActivity is not type of SOURCE");
        }
        HandlerBuilder handlerBuilder = ActivityBuilder
                .handler(handlerDto.getName())
                .from(ActivityBuilder
                        .fromSource(fromSourceActivity.getName(), fromSourceActivity.getSourceName()).build())
                .outputMapping(handlerDto.getOutputMapping());

        activityService.buildGroupBuilder(handlerBuilder, handlerDto.getGroup());
        return handlerBuilder.build();
    }
}
