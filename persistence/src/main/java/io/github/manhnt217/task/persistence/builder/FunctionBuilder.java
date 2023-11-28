package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.task.function.Function;

import static io.github.manhnt217.task.core.task.function.Function.*;

/**
 * @author manh nguyen
 */
public class FunctionBuilder extends AbstractFunctionBuilder<Function> implements LinkedActivityGroupBuilder<FunctionBuilder> {

    private GroupBuilder groupBuilder;

    FunctionBuilder(String name) {
        this.name = name;
        groupBuilder = new GroupBuilder();
        groupBuilder.start(START_ACTIVITY_NAME);
        groupBuilder.end(END_ACTIVITY_NAME);
    }

    @Override
    public Function build() throws ConfigurationException {
        groupBuilder.validate();
        return new Function(name, groupBuilder.buildGroup());
    }

    @Override
    public GroupBuilder getGroupBuilder() {
        return this.groupBuilder;
    }
}
