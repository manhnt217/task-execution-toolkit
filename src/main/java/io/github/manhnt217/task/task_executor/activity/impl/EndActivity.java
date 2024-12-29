/*
 * @author manh nguyen
 */

package io.github.manhnt217.task.task_executor.activity.impl;

import io.github.manhnt217.task.task_executor.activity.*;
import io.github.manhnt217.task.task_executor.context.ActivityContext;
import io.github.manhnt217.task.task_executor.task.CompositeTask;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author manhnguyen
 */
@Getter
@Setter
public class EndActivity implements Activity {

    private final String name;
    private String inputMapping;

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
