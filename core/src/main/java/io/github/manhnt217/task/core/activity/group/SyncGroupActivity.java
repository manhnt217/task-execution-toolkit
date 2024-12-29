package io.github.manhnt217.task.core.activity.group;

import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.exception.ActivityException;

public class SyncGroupActivity extends GroupActivity {

    public SyncGroupActivity(String name, Group activityGroup) {
        super(name, activityGroup);
    }

    @Override
    public synchronized OutboundMessage process(InboundMessage in, ActivityContext context) throws ActivityException {
        return super.process(in, context);
    }
}
