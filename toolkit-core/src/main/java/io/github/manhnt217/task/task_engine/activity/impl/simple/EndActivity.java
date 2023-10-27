/*
 * @author manh nguyen
 */

package io.github.manhnt217.task.task_engine.activity.impl.simple;

import io.github.manhnt217.task.task_engine.activity.*;
import io.github.manhnt217.task.task_engine.activity.impl.SimpleOutboundMessage;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public class EndActivity implements Activity {

    private final String name;
    private String inputMapping;
    private ActivityGroup<?, ?> parent;

    public EndActivity(String name) {
        this.name = name;
    }

    @Override
    public boolean registerOutput() {
        return false;
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityLogger activityLogger, ActivityContext context) {
        return SimpleOutboundMessage.of(in.getContent());
    }
}
