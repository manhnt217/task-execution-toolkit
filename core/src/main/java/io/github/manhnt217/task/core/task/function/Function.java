package io.github.manhnt217.task.core.task.function;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.TaskException;
import io.github.manhnt217.task.core.exception.GroupException;
import io.github.manhnt217.task.core.task.Task;
import io.github.manhnt217.task.core.task.TaskContext;
import lombok.Getter;

/**
 * A task that contains multiple activities being linked together
 * @author manh nguyen
 */
@Getter
public class Function implements Task {

    public static final String START_ACTIVITY_NAME = "START";
    public static final String END_ACTIVITY_NAME = "END";

    public static final String START_IM = "." + START_ACTIVITY_NAME;
    private final String name;

    private final Group activityGroup;

    public Function(String name, Group activityGroup) {
        this.name = name;
        this.activityGroup = activityGroup;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public JsonNode call(JsonNode input, TaskContext context) throws TaskException {
        context.setTaskName(this.name);
        try {
            return activityGroup.execute(input, context);
        } catch (GroupException e) {
            throw new TaskException(getName(), input, e);
        } catch (ActivityException e) {
            throw new TaskException(getName(), input, e);
        }
    }
}
