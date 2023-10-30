package io.github.manhnt217.task.task_engine.activity.group;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_engine.activity.Activity;
import io.github.manhnt217.task.task_engine.activity.ActivityLogger;
import io.github.manhnt217.task.task_engine.activity.InboundMessage;
import io.github.manhnt217.task.task_engine.activity.OutboundMessage;
import io.github.manhnt217.task.task_engine.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.task_engine.activity.simple.AbstractSimpleActivity;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.context.sub.GroupContext;
import io.github.manhnt217.task.task_engine.exception.ActivityException;
import io.github.manhnt217.task.task_engine.exception.GroupException;

/**
 * @author manhnguyen
 */
public class GroupActivity extends AbstractSimpleActivity implements Activity {

    private final Group activityGroup;
    public GroupActivity(String name, Group activityGroup) {
        super(name);
        this.activityGroup = activityGroup;
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityLogger activityLogger, ActivityContext context) throws ActivityException {
        try {
            JsonNode output = activityGroup.execute(in.getContent(), activityLogger, new GroupContext(context));
            return SimpleOutboundMessage.of(output);
        } catch (GroupException e) {
            throw new ActivityException(this, "Group execution failed", e);
        }
    }
}
