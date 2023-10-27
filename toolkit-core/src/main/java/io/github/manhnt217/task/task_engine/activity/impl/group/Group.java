package io.github.manhnt217.task.task_engine.activity.impl.group;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_engine.activity.*;
import io.github.manhnt217.task.task_engine.activity.impl.SimpleOutboundMessage;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.exception.ActivityException;
import io.github.manhnt217.task.task_engine.exception.GroupException;
import io.github.manhnt217.task.task_engine.context.sub.GroupContext;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public class Group extends AbstractGroup implements Activity {

    public Group(String name, String startActivityName, String endActivityName, String outputMapping) throws ConfigurationException {
        super(name, startActivityName, endActivityName, outputMapping);
    }

    public Group(String name, String startActivityName, String endActivityName) throws ConfigurationException {
        this(name, startActivityName, endActivityName, null);
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityLogger activityLogger, ActivityContext context) throws ActivityException {
        try {
            JsonNode output = this.execute(in.getContent(), activityLogger, new GroupContext(context));
            return SimpleOutboundMessage.of(output);
        } catch (GroupException e) {
            throw new ActivityException(this, "Group execution failed", e);
        }
    }
}
