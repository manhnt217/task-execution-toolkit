/*
 * @author manh nguyen
 */

package io.github.manhnt217.task.core.activity.simple;

import io.github.manhnt217.task.core.activity.AbstractActivity;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.core.context.ActivityContext;
import lombok.Getter;
import lombok.Setter;

/**
 * @author manh nguyen
 */
@Getter
@Setter
public class EndActivity extends AbstractActivity {

    public EndActivity(String name) {
        super(name);
    }

    @Override
    public boolean registerOutput() {
        return false;
    }

    @Override
    public OutboundMessage process(InboundMessage in, ActivityContext context) {
        return SimpleOutboundMessage.of(in.getContent());
    }
}
