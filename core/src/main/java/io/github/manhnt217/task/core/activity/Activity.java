package io.github.manhnt217.task.core.activity;

import io.github.manhnt217.task.core.activity.group.ActivityGroup;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.ActivityInputException;

import java.util.Collections;
import java.util.Set;

/**
 * @author manh nguyen
 */
public interface Activity {

    String getName();

    default Set<String> getContainedActivityNames() {
        return Collections.singleton(getName());
    }

    String getInputMapping() throws ActivityInputException;

    ActivityGroup<?, ?> getParent();

    void setParent(ActivityGroup<?, ?> parent);

    default boolean registerOutput() {
        return true;
    }

    /**
     * @param in
     * @param context
     * @return <code>null</code> means the activity has done processing the input,
     * but hasn't returned any output yet due to some certain criteria are not met
     */
    OutboundMessage process(InboundMessage in, ActivityContext context) throws ActivityException;
}
