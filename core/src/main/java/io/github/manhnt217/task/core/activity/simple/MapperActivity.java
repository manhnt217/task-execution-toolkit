package io.github.manhnt217.task.core.activity.simple;

import io.github.manhnt217.task.core.activity.AbstractActivity;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.exception.ActivityException;

/**
 * @author manhnguyen
 */
public class MapperActivity extends AbstractActivity {
	public MapperActivity(String name) {
		super(name);
	}

	@Override
	public OutboundMessage process(InboundMessage in, ActivityContext context) throws ActivityException {
		return SimpleOutboundMessage.of(in.getContent());
	}
}
