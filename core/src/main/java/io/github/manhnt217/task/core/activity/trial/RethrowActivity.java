package io.github.manhnt217.task.core.activity.trial;

import io.github.manhnt217.task.core.activity.AbstractActivity;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.exception.ActivityException;

public class RethrowActivity extends AbstractActivity {

    public RethrowActivity(String name) {
        super(name);
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityContext context) throws ActivityException {
        String message;
        Exception exception;
        try {
            Rethrow rethrow = JSONUtil.treeToValue(in.getContent(), Rethrow.class, context);
            message = rethrow.getMessage();
            exception = rethrow.getEx().get();
        } catch (Exception e) {
            throw new ThrowActivityException(context.getCurrentTaskName(), this.getName(), "Cannot deserialize the input to Throw object. Input = " + in.getContent(), e);
        }
        throw new CustomActivityException(context.getCurrentTaskName(), this.getName(), message, exception);
    }
}
