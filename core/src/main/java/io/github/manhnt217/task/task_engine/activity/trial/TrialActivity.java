package io.github.manhnt217.task.task_engine.activity.trial;

import io.github.manhnt217.task.task_engine.activity.Activity;
import io.github.manhnt217.task.task_engine.activity.ActivityLogger;
import io.github.manhnt217.task.task_engine.activity.InboundMessage;
import io.github.manhnt217.task.task_engine.activity.OutboundMessage;
import io.github.manhnt217.task.task_engine.activity.AbstractActivity;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.exception.ActivityException;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public class TrialActivity extends AbstractActivity implements Activity {

    public TrialActivity(String name) {
        super(name);
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityLogger activityLogger, ActivityContext context) throws ActivityException {
        return null;
    }
}
