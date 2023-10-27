package io.github.manhnt217.task.task_engine.activity.impl.trial;

import io.github.manhnt217.task.task_engine.activity.Activity;
import io.github.manhnt217.task.task_engine.activity.ActivityLogger;
import io.github.manhnt217.task.task_engine.activity.InboundMessage;
import io.github.manhnt217.task.task_engine.activity.OutboundMessage;
import io.github.manhnt217.task.task_engine.activity.impl.group.AbstractGroup;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.exception.ActivityException;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public class TrialActivity extends AbstractGroup implements Activity {

    public TrialActivity(String name, String startActivityName, String endActivityName, String outputMapping) throws ConfigurationException {
        super(name, startActivityName, endActivityName, outputMapping);
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityLogger activityLogger, ActivityContext context) throws ActivityException {
        return null;
    }
}
