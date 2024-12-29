package io.github.manhnt217.task.task_engine.persistence.builder;

import io.github.manhnt217.task.task_engine.activity.Activity;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author manhnguyen
 */
public abstract class AbstractActivityBuilder<A extends Activity, B extends AbstractActivityBuilder<A, B>> {

    protected String name;
    protected String inputMapping;

    public B name(String name) {
        this.name = name;
        return (B) this;
    }

    public B inputMapping(String inputMapping) {
        this.inputMapping = inputMapping;
        return (B) this;
    }

    protected void validate() {
        if (StringUtils.isBlank(this.name)) {
            throw new IllegalArgumentException("Activity's name should not be empty");
        }
    }

    public abstract A build() throws ConfigurationException;
}
