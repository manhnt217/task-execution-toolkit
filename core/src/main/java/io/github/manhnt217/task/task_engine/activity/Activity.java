package io.github.manhnt217.task.task_engine.activity;

import io.github.manhnt217.task.task_engine.activity.group.ActivityGroup;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.exception.ActivityException;

import java.util.Collections;
import java.util.Set;

/**
 * @author manhnguyen
 */
public interface Activity {

    String getName();

    default Set<String> getContainedActivityNames() {
        return Collections.singleton(getName());
    }

    String getInputMapping();

    ActivityGroup<?, ?> getParent();

    void setParent(ActivityGroup<?, ?> parent);

    default boolean registerOutput() {
        return true;
    }

    /**
     * @param in
     * @param activityLogger
     * @param context
     * @return <code>null</code> means the activity has done processing the input,
     * but hasn't returned any output yet due to some certain criteria are not met
     */
    OutboundMessage process(InboundMessage in, ActivityLogger activityLogger, ActivityContext context) throws ActivityException;
}
