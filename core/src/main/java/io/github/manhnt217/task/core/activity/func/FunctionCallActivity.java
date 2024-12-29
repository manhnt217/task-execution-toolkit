package io.github.manhnt217.task.core.activity.func;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.AbstractActivity;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.context.Callstack;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.task.TaskException;
import io.github.manhnt217.task.core.task.TaskContext;
import io.github.manhnt217.task.core.task.function.Function;
import lombok.Setter;

/**
 * @author manh nguyen
 */
@Setter
public class FunctionCallActivity extends AbstractActivity {

    private final String functionName;

    public FunctionCallActivity(String name, String functionName) {
        super(name);
        this.functionName = functionName;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public OutboundMessage process(InboundMessage in, ActivityContext context) throws ActivityException {
        JsonNode input = in.getContent();
        Function function;
        try {
            function = context.getRepo().getFunction(functionName);
        } catch (Exception e) {
            throw new FunctionCallActivityException(context.getCurrentTaskName(), this.getName(), "Cannot resolve function '" + functionName + "'", e);
        }
        if (function == null) {
            throw new FunctionCallActivityException(context.getCurrentTaskName(), this.getName(), "Function '" + functionName + "' is not found", null);
        }

        Object funcIn;
        try {
            funcIn = JSONUtil.treeToValue(input, function.getInputType(), context);
        } catch (Exception e) {
            throw new FunctionCallActivityException(context.getCurrentTaskName(), this.getName(), "Exception while converting input for function '" + functionName + "'. Input = " + input, e);
        }

        Object output;
        try {
            output = function.exec(funcIn, context);
        } catch (TaskException e) {
            throw new FunctionCallActivityException(e.getTaskName(), this.getName(), "Activity '" + this.getName() + "' cannot be executed because: An exception was thrown in function '" + e.getTaskName() + "'", e);
        }

        try {
            return SimpleOutboundMessage.of(JSONUtil.valueToTree(output, context));
        } catch (Exception e) {
            throw new FunctionCallActivityException(context.getCurrentTaskName(), this.getName(),  "Exception while converting output for function '" + functionName + "'", e);
        }
    }
}
