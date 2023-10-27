package io.github.manhnt217.task.task_engine.activity.impl.group;

import io.github.manhnt217.task.task_engine.activity.Activity;
import io.github.manhnt217.task.task_engine.activity.ActivityGroup;
import io.github.manhnt217.task.task_engine.activity.impl.LinkBasedActivityGroup;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public abstract class AbstractGroup extends LinkBasedActivityGroup implements Activity {

    private final String name;
    private String inputMapping;
    private ActivityGroup<?, ?> parent;

    public AbstractGroup(String name, String startActivityName, String endActivityName, String outputMapping) throws ConfigurationException {
        super(startActivityName, endActivityName, outputMapping);
        this.name = name;
    }

    @Override
    public boolean registerOutput() {
        return true;
    }
}
