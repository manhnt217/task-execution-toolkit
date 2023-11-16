/*
 * @author manh nguyen
 */

package io.github.manhnt217.task.core.activity.simple;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.*;
import io.github.manhnt217.task.core.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.core.context.ActivityContext;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public class StartActivity extends AbstractActivity {

    private JsonNode output;

    public StartActivity(String name) {
        super(name);
    }

    @Override
    public String getInputMapping() {
        return null;
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityLogger activityLogger, ActivityContext context) {
        return SimpleOutboundMessage.of(output);
    }
}
