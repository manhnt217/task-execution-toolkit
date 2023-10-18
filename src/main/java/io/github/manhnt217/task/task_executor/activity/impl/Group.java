package io.github.manhnt217.task.task_executor.activity.impl;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.activity.*;
import io.github.manhnt217.task.task_executor.context.ActivityContext;
import io.github.manhnt217.task.task_executor.context.GroupContext;
import lombok.Getter;

/**
 * @author manhnguyen
 */
@Getter
public class Group extends LinkBasedActivityGroup implements Activity {

    private final String name;
    private String inputMapping;

    public Group(String name, String startActivityName, String endActivityName) {
        super(startActivityName, endActivityName);
        this.name = name;
    }

    @Override
    public boolean registerOutput() {
        return true;
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityLogger activityLogger, ActivityContext context) throws ActivityExecutionException {
        try {
            JsonNode output = this.execute(in.getContent(), activityLogger, new GroupContext(context));
            return SimpleOutboundMessage.of(output);
        } catch (ExecutionException e) {
            throw new ActivityExecutionException(this, "Group execution failed", e);
        }
    }
}
