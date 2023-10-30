package io.github.manhnt217.task.task_engine.activity.simple;

import io.github.manhnt217.task.task_engine.activity.Activity;
import io.github.manhnt217.task.task_engine.activity.group.ActivityGroup;
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
    private ActivityGroup<?, ?> parent;

    public AbstractSimpleActivity(String name) {
        this.name = name;
    }

    @Override
    public boolean registerOutput() {
        return true;
    }
}
