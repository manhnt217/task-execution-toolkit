package io.github.manhnt217.task.core.activity.trial;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.AbstractGroupActivity;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.exception.ActivityException;

/**
 * @author manh nguyen
 */
public class TrialActivity extends AbstractGroupActivity {

    // TODO: A note for activity design
    //  Properties that won't change in runtime will become Activity's fields (class variables)
    //  Anything else should be passed through Activity's input (#io.github.manhnt217.task.core.activity.InboundMessage)

    private final Class<? extends Throwable> ex;

    private final boolean catchRootCause;

    public TrialActivity(String name, Group activityGroup, Class<? extends Throwable> ex, boolean catchRootCause) {
        super(name, activityGroup);
        this.ex = ex;
        this.catchRootCause = catchRootCause;
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityContext context) throws ActivityException {
        Trial trial = trial(in, context);
        return SimpleOutboundMessage.of(JSONUtil.valueToTree(trial, context));
    }

    private Trial trial(InboundMessage in, ActivityContext context) throws ActivityException {
        try {
            JsonNode output = activityGroup.execute(in.getContent(), new TrialContext(context));
            return Trial.success(output);
        } catch (ActivityException e) {
            if (this.catchRootCause) {
                Throwable rootCause = e.getRootCause();
                return catchException(rootCause, e);
            }
            else return catchException(e, e);
        }
    }

    private Trial catchException(Throwable exToCatch, ActivityException exToThrow) throws ActivityException {
        if (ex.isInstance(exToCatch)) {
            return Trial.failure(exToCatch);
        } else throw exToThrow;
    }
}
