package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.activity.source.FromSourceActivity;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.task.handler.Handler;

import static io.github.manhnt217.task.core.task.function.Function.END_ACTIVITY_NAME;

/**
 * @author manh nguyen
 */
public class HandlerBuilder extends AbstractFunctionBuilder<Handler> implements LinkedActivityGroupBuilder<HandlerBuilder> {

    private GroupBuilder groupBuilder;

    HandlerBuilder(String name) {
        this.name = name;
        groupBuilder = new GroupBuilder();
        groupBuilder.end(END_ACTIVITY_NAME);
    }

    public HandlerBuilder from(FromSourceActivity fromSourceActivity) {
        groupBuilder.start(fromSourceActivity);
        return this;
    }

    @Override
    public Handler build() throws ConfigurationException {
        groupBuilder.validate();
        return new Handler(name, groupBuilder.buildGroup());
    }

    @Override
    public GroupBuilder getGroupBuilder() {
        return this.groupBuilder;
    }
}
