package io.github.manhnt217.task.task_engine.activity;

import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.exception.ActivityException;

/**
 * @author manhnguyen
 */
public interface Activity {

    String getName();

    String getInputMapping();

    ActivityGroup<?, ?> getParent();

    void setParent(ActivityGroup<?, ?> parent);

    /**
     * Specify whether the output of this activity will be recorded for later use
     *
     * @return
     */
    boolean registerOutput();

    /**
     * @param in
     * @param activityLogger
     * @param context
     * @return <code>null</code> means the activity has done processing the input,
     * but hasn't returned any output yet due to some certain criteria are not met
     */
    OutboundMessage process(InboundMessage in, ActivityLogger activityLogger, ActivityContext context) throws ActivityException;
}
