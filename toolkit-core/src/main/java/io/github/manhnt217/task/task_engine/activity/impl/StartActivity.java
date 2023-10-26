/*
 * @author manh nguyen
 */

package io.github.manhnt217.task.task_engine.activity.impl;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_engine.activity.*;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public class StartActivity implements Activity {

    private JsonNode output;
    private final String name;
    private ActivityGroup<?, ?> parent;

    public StartActivity(String name) {
        this.name = name;
    }

    @Override
    public String getInputMapping() {
        return null;
    }

    @Override
    public boolean registerOutput() {
        return true;
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityLogger activityLogger, ActivityContext context) {
        return SimpleOutboundMessage.of(output);
    }
}
