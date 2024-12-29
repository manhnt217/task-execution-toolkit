package io.github.manhnt217.task.task_executor.task;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.activity.Activity;
import io.github.manhnt217.task.task_executor.activity.ActivityExecutionException;
import io.github.manhnt217.task.task_executor.activity.ActivityLogger;
import io.github.manhnt217.task.task_executor.activity.impl.LinkBasedActivityGroup;
import io.github.manhnt217.task.task_executor.context.ActivityContext;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author manhnguyen
 */
@Getter
public class CompositeTask extends LinkBasedActivityGroup implements Task {

    public static final String START_DEFAULT_NAME = "START";
    public static final String END_DEFAULT_NAME = "END";
    private final String name;

    // TODO: Should be removed. It exists for testing purpose only
    @Deprecated
    public CompositeTask(String name, List<Activity> childActivities) {
        super(START_DEFAULT_NAME, END_DEFAULT_NAME);
        this.name = name;
        if (CollectionUtils.isEmpty(childActivities)) {
            return;
        }
        this.addActivity(childActivities.get(0));
        this.linkActivities(startActivity, childActivities.get(0), null);
        for (int i = 0; i < childActivities.size() - 1; i++) {
            link(childActivities.get(i), childActivities.get(i + 1));
        }
        linkActivities(childActivities.get(childActivities.size() - 1), endActivity, null);
    }

    public CompositeTask(String name) {
        this(name, null);
    }

    private void link(Activity from, Activity to) {
        this.addActivity(to);
        this.linkActivities(from, to, null);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public JsonNode run(JsonNode input, String activityName, ActivityLogger activityLogger, ActivityContext context) throws TaskExecutionException {
        try {
            return this.execute(input, activityLogger, context);
        } catch (ActivityExecutionException e) {
            throw new TaskExecutionException(getName(), input, "Exception was thrown from inner activities", e);
        }
    }
}
