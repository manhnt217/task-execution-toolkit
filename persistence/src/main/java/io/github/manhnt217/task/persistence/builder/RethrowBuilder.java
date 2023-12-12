package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.activity.trial.RethrowActivity;

/**
 * @author manh nguyen
 */
public class RethrowBuilder extends AbstractActivityBuilder<RethrowActivity, RethrowBuilder> {

    private final String name;

    RethrowBuilder(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public RethrowActivity build() {
        RethrowActivity activity = new RethrowActivity(name);
        activity.setInputMapping(inputMapping);
        return activity;
    }
}
