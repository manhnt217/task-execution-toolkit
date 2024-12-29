package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.activity.future.WaitActivity;

/**
 * @author manh nguyen
 */
public class WaitBuilder extends AbstractActivityBuilder<WaitActivity, WaitBuilder> {

    private final String name;

    WaitBuilder(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public WaitActivity build() {
        WaitActivity waitActivity = new WaitActivity(name);
        waitActivity.setInputMapping(inputMapping);
        return waitActivity;
    }
}
