package io.github.manhnt217.task.task_engine.activity;

import io.github.manhnt217.task.task_engine.activity.group.ActivityGroup;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manh nguyen
 */
@Getter
@Setter
public abstract class AbstractActivity implements Activity {

    protected final String name;
    protected String inputMapping;
    protected ActivityGroup<?, ?> parent;

    public AbstractActivity(String name) {
        this.name = name;
    }
}
