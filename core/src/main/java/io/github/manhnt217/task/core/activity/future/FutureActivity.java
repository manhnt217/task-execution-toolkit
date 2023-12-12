package io.github.manhnt217.task.core.activity.future;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.AbstractGroupActivity;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.container.FutureProcessor;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.type.Future;
import io.github.manhnt217.task.core.type.ObjectRef;

public class FutureActivity extends AbstractGroupActivity {
    public FutureActivity(String name, Group activityGroup) {
        super(name, activityGroup);
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityContext context) throws ActivityException {
        FutureProcessor futureProcessor = context.getFutureProcessor();
        Future<JsonNode> future = futureProcessor.submit(() -> activityGroup.execute(in.getContent(), new FutureContext(context)));
        JsonNode output = JSONUtil.valueToTree(new ObjectRef<>(future), context);
        return SimpleOutboundMessage.of(output);
    }

}
