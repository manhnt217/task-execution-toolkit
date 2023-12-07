package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.activity.source.FromSourceActivity;

public class FromSourceBuilder extends AbstractActivityBuilder<FromSourceActivity, FromSourceBuilder> {

    private final String sourceName;

    public FromSourceBuilder(String name, String sourceName) {
        super(name);
        this.sourceName = sourceName;
    }

    @Override
    public FromSourceActivity build() {
        return new FromSourceActivity(name, sourceName);
    }
}
