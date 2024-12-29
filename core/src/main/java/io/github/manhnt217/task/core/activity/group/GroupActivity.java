package io.github.manhnt217.task.core.activity.group;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.AbstractGroupActivity;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.exception.ActivityException;

/**
 * @author manh nguyen
 */
public class GroupActivity extends AbstractGroupActivity {

    public GroupActivity(String name, Group activityGroup) {
        super(name, activityGroup);
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityContext context) throws ActivityException {
        JsonNode output = activityGroup.execute(in.getContent(), new GroupContext(context));
        return SimpleOutboundMessage.of(output);
    }
}
