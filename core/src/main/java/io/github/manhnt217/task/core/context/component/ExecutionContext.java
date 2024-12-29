package io.github.manhnt217.task.core.context.component;

import io.github.manhnt217.task.core.activity.ActivityInfo;
import io.github.manhnt217.task.core.context.Callstack;

import java.util.List;

/**
 * @author manhnguyen
 */
public interface ExecutionContext {

	Callstack getCallStack();

	default String getCurrentTaskName() {
		return getCallStack().getTop();
	}
	/**
	 * @return the list of executed activities in current context
	 */
	List<ActivityInfo> getExecutedActivities();
}
