package io.github.manhnt217.task.core.activity.source;

import io.github.manhnt217.task.core.activity.simple.StartActivity;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author manh nguyen
 */
public class FromSourceActivity extends StartActivity {

    @Getter
    private final String sourceName;

    public FromSourceActivity(String name, String sourceName) {
        super(name);
        if (StringUtils.isBlank(sourceName)) {
            throw new IllegalArgumentException("Source's name cannot be empty");
        }
        this.sourceName = sourceName;
    }
}
