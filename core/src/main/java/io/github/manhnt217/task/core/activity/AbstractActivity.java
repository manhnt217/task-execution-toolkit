package io.github.manhnt217.task.core.activity;

import io.github.manhnt217.task.core.activity.group.ActivityGroup;
import io.github.manhnt217.task.core.exception.ActivityInputException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

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
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Activity's name cannot be empty");
        }
        this.name = name;
    }

    public String getInputMapping() throws ActivityInputException {
        return inputMapping;
    }
}
