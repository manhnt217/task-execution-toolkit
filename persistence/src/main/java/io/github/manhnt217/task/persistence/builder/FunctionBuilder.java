package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.task.function.Function;

import static io.github.manhnt217.task.core.task.function.Function.*;

/**
 * @author manh nguyen
 */
public class FunctionBuilder<P, R> extends AbstractFunctionBuilder<Function<P, R>> implements LinkedActivityGroupBuilder<FunctionBuilder<P, R>> {

    private final GroupBuilder groupBuilder;
    private final Class<? extends P> inputType;
    private final Class<? extends R> outputType;

    FunctionBuilder(String name, Class<? extends P> inputClass, Class<? extends R> outputClass) {
        this.name = name;
        this.inputType = inputClass;
        this.outputType = outputClass;
        groupBuilder = new GroupBuilder();
        groupBuilder.start(START_ACTIVITY_NAME);
        groupBuilder.end(END_ACTIVITY_NAME);
    }

    @Override
    public Function<P, R> build() throws ConfigurationException {
        groupBuilder.validate();
        return new Function<>(name, groupBuilder.buildGroup(), inputType, outputType);
    }

    @Override
    public GroupBuilder getGroupBuilder() {
        return this.groupBuilder;
    }
}
