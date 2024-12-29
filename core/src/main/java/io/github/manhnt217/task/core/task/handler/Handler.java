package io.github.manhnt217.task.core.task.handler;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.activity.simple.StartActivity;
import io.github.manhnt217.task.core.activity.source.FromSourceActivity;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.context.Callstack;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.task.Task;
import io.github.manhnt217.task.core.task.TaskContext;
import io.github.manhnt217.task.core.task.TaskException;
import lombok.Getter;

/**
 * @author manh nguyen
 */
// TODO: We may add type for Handler, just like we did with #io.github.manhnt217.task.core.task.function.Function
public class Handler<E, R> implements Task {

    @Getter
    private final String name;
    @Getter
    private final String sourceName;

    private final Group activityGroup;

    @Getter
    protected final Class<? extends E> eventType;
    @Getter
    protected final Class<? extends R> outputType;

    public Handler(String name, Group activityGroup, Class<? extends E> eventType, Class<? extends R> outputType) {
        this.name = name;
        this.activityGroup = activityGroup;
        this.eventType = eventType;
        this.outputType = outputType;
        StartActivity startActivity = activityGroup.getStartActivity();
        if (startActivity instanceof FromSourceActivity) {
            sourceName = ((FromSourceActivity) startActivity).getSourceName();
        } else {
            throw new IllegalArgumentException("Handler's activity group must contain a " + FromSourceActivity.class.getSimpleName());
        }
    }

    public R handle(E in, ActivityContext context) throws TaskException, ActivityException {

        TaskContext handlerContext = new TaskContext(
                context.getExecutionId(),
                Callstack.push(this.name, context.getCallStack()),
                context.getProps(),
                context.getRepo(),
                context.getFutureProcessor(),
                context.getLogger());
        JsonNode input;
        try {
            input = JSONUtil.valueToTree(in, handlerContext);
        } catch (Exception e) {
            throw new TaskException(getName(), null, "Cannot serialize input to Json. Input = " + in, e);
        }
        handlerContext.setTaskName(name);
        JsonNode groupOutput = activityGroup.execute(input, handlerContext);
        if (outputType == null || Void.class.equals(outputType)) {
            return null;
        }
        try {
            return JSONUtil.treeToValue(groupOutput, outputType, handlerContext);
        } catch (Exception e) {
            throw new TaskException(getName(), input, "Cannot convert output to desired type. Output = " + groupOutput + ". Desired type = " + outputType, e);
        }
    }
}
