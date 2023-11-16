package io.github.manhnt217.task.core.activity.trial;

import io.github.manhnt217.task.core.activity.AbstractGroupActivity;
import io.github.manhnt217.task.core.activity.ActivityLogger;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.exception.ActivityException;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public class TrialActivity extends AbstractGroupActivity {

    public TrialActivity(String name, Group activityGroup) {
        super(name, activityGroup);
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityLogger activityLogger, ActivityContext context) throws ActivityException {
        return null;
    }
}
