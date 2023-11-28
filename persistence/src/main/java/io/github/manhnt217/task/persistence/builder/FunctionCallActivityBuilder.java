package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.activity.func.FunctionCallActivity;

/**
 * @author manh nguyen
 */
public class FunctionCallActivityBuilder extends AbstractActivityBuilder<FunctionCallActivity, FunctionCallActivityBuilder> {

    private String functionName;

    FunctionCallActivityBuilder(String name) {
        this.name = name;
    }

    public FunctionCallActivityBuilder funcName(String func) {
        this.functionName = func;
        return this;
    }

    @Override
    public FunctionCallActivity build() {
        FunctionCallActivity functionCallActivity = new FunctionCallActivity(name, functionName);
        functionCallActivity.setInputMapping(inputMapping);
        return functionCallActivity;
    }
}
