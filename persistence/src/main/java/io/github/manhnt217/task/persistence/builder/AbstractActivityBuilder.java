package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.activity.Activity;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author manh nguyen
 */
public abstract class AbstractActivityBuilder<A extends Activity, B extends AbstractActivityBuilder<A, B>> {

    protected final String name;
    protected String inputMapping;

    protected AbstractActivityBuilder(String name) {
        this.name = name;
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
