/*
 * @author manh nguyen
 */

package io.github.manhnt217.task.core.activity.simple;

import io.github.manhnt217.task.core.context.component.ExecutionContext;
import io.github.manhnt217.task.core.exception.ActivityException;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manh nguyen
 */
@Getter
@Setter
public class EndActivity extends FromLastActivity {

    public EndActivity(String name) {
        super(name);
    }

    @Override
    public boolean registerOutput() {
        return false;
    }

    @Override
    public void withExecutionContext(ExecutionContext context) throws ActivityException {
        if (this.inputMapping != null) {
            return;
        }
        super.withExecutionContext(context);
    }
}
