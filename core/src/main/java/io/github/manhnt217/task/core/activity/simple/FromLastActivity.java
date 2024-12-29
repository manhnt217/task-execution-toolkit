package io.github.manhnt217.task.core.activity.simple;

import io.github.manhnt217.task.core.activity.ActivityInfo;
import io.github.manhnt217.task.core.activity.ExecutionAwareActivity;
import io.github.manhnt217.task.core.context.component.ExecutionContext;
import io.github.manhnt217.task.core.exception.ActivityException;

import java.util.List;

import static io.github.manhnt217.task.core.context.ActivityContext.from;

/**
 * This special activity will take whatever is the last activity's output in current context
 * and return it as its output.
 * @author manhnguyen
 */
public class FromLastActivity extends MapperActivity implements ExecutionAwareActivity {
	public FromLastActivity(String name) {
		super(name);
	}

	@Override
	public void withExecutionContext(ExecutionContext context) throws ActivityException {
		List<ActivityInfo> executedActivities = context.getExecutedActivities();
		for (int i = executedActivities.size() - 1; i >= 0; i--) {
			ActivityInfo activityInfo = executedActivities.get(i);
			if (activityInfo.hasOutput()) {
				this.setInputMapping(from(activityInfo.getName()));
				return;
			}
		}
		// This is technically cannot happen because there is always at least one activity that has output (StartActivity)
		throw new ActivityException(context.getCurrentTaskName(), this.getName(), "Cannot retrieve last activity");
	}
}
