package io.github.manhnt217.task.core.activity.group;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.AbstractGroupActivity;
import io.github.manhnt217.task.core.activity.ActivityLogger;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.context.sub.GroupContext;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.GroupException;

/**
 * @author manhnguyen
 */
public class GroupActivity extends AbstractGroupActivity {

    public GroupActivity(String name, Group activityGroup) {
        super(name, activityGroup);
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
