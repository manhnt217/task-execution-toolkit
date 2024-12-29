package io.github.manhnt217.task.core.activity.future;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.AbstractGroupActivity;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.GroupException;
import io.github.manhnt217.task.core.type.Future;
import io.github.manhnt217.task.core.type.ObjectRef;

public class FutureActivity extends AbstractGroupActivity {
    public FutureActivity(String name, Group activityGroup) {
        super(name, activityGroup);
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityContext context) throws ActivityException {
        ObjectRef<Future<JsonNode>> futureRef = new ObjectRef<>(context.getFutureProcessor().submit(() -> executeGroup(in, context)));
        JsonNode output = JSONUtil.valueToTree(futureRef, context);
        return SimpleOutboundMessage.of(output);
    }

    private JsonNode executeGroup(InboundMessage in, ActivityContext context) throws ActivityException {
        try {
            JsonNode result = activityGroup.execute(in.getContent(), new FutureContext(context));
            return result;
        } catch (GroupException e) {
            throw new ActivityException(this, "Group execution failed", e);
        }
    }
}
