/*
 * @author manh nguyen
 */

package io.github.manhnt217.task.task_engine.activity.simple;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_engine.activity.*;
import io.github.manhnt217.task.task_engine.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.task_engine.activity.group.ActivityGroup;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
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
