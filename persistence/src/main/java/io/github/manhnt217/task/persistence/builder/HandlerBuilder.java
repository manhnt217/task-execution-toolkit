package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.activity.source.FromSourceActivity;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.task.handler.Handler;

import static io.github.manhnt217.task.core.task.function.Function.*;

/**
 * @author manh nguyen
 */
public class HandlerBuilder<E, R> extends AbstractFunctionBuilder<Handler> implements LinkedActivityGroupBuilder<HandlerBuilder<E, R>> {

    private GroupBuilder groupBuilder;

    protected final Class<? extends E> eventType;
    protected final Class<? extends R> outputType;

    HandlerBuilder(String name, Class<? extends E> eventType, Class<? extends R> outputType) {
        this.eventType = eventType;
        this.outputType = outputType;
        this.name = name;
        groupBuilder = new GroupBuilder();
        groupBuilder.end(END_ACTIVITY_NAME);
    }

    public HandlerBuilder<E, R> from(FromSourceActivity fromSourceActivity) {
        groupBuilder.start(fromSourceActivity);
        return this;
    }

    @Override
    public Handler<E, R> build() throws ConfigurationException {
        groupBuilder.validate();
        return new Handler<>(name, groupBuilder.buildGroup(), eventType, outputType);
    }

    @Override
    public GroupBuilder getGroupBuilder() {
        return this.groupBuilder;
    }
}
