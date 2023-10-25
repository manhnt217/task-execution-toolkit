package io.github.manhnt217.task.task_engine.activity.impl;

import io.github.manhnt217.task.task_engine.activity.Activity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manh nguyen
 */
@Getter
@Setter
public abstract class AbstractSimpleActivity implements Activity {

    private final String name;
    private String inputMapping;

    public AbstractSimpleActivity(String name) {
        this.name = name;
    }

    @Override
    public boolean registerOutput() {
        return true;
    }
}
