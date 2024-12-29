package io.github.manhnt217.task.core.activity;

import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.context.component.ExecutionContext;
import io.github.manhnt217.task.core.exception.ActivityException;

/**
 * @author manhnguyen
 */
public interface ExecutionAwareActivity extends Activity {

	/**
	 * This method will be executed right before {@link #process(InboundMessage, ActivityContext)}
	 * @param context ExecutionContext
	 */
	void withExecutionContext(ExecutionContext context) throws ActivityException;
}
