package io.github.manhnt217.task.core.activity.simple;

import io.github.manhnt217.task.core.activity.ActivityInfo;
import io.github.manhnt217.task.core.activity.ExecutionAwareActivity;
import io.github.manhnt217.task.core.context.component.ExecutionContext;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.ActivityInputException;

import java.util.List;

import static io.github.manhnt217.task.core.context.ActivityContext.from;

/**
 * This special activity will take whatever is the last activity's output in current context
 * and return it as its output.
 * @author manhnguyen
 */
public class FromLastActivity extends MapperActivity implements ExecutionAwareActivity {

	private ExecutionContext context;

	public FromLastActivity(String name) {
		super(name);
	}

	@Override
	public String getInputMapping() throws ActivityInputException {
		if (this.inputMapping != null) {
			return this.inputMapping;
		}
		List<ActivityInfo> executedActivities = context.getExecutedActivities();
		for (int i = executedActivities.size() - 1; i >= 0; i--) {
			ActivityInfo activityInfo = executedActivities.get(i);
			if (activityInfo.hasOutput()) {
				return from(activityInfo.getName());
			}
		}
		// This is technically cannot happen because there is always at least one activity that has output (StartActivity)
		throw new ActivityInputException(context.getCurrentTaskName(), this.getName(), new RuntimeException("Cannot retrieve last activity"));
	}

	@Override
	public void withExecutionContext(ExecutionContext context) throws ActivityException {
		this.context = context;
	}
}
