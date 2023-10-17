/*
 * @author manh nguyen
 */

package io.github.manhnt217.task.task_executor.activity.impl;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.activity.*;
import io.github.manhnt217.task.task_executor.context.ActivityContext;
import io.github.manhnt217.task.task_executor.task.CompositeTask;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import static io.github.manhnt217.task.task_executor.context.ActivityContext.OBJECT_MAPPER;

/**
 * @author manhnguyen
 */
@Getter
public class StartActivity implements Activity {

    private JsonNode output = OBJECT_MAPPER.createObjectNode();
    private final String name;

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

    public void setOutput(JsonNode output) {
        if (output != null) {
            this.output = output;
        }
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityLogger activityLogger, ActivityContext context) {
        return SimpleOutboundMessage.of(output);
    }
}
