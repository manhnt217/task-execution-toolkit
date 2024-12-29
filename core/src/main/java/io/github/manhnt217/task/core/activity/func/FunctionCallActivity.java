package io.github.manhnt217.task.core.activity.func;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.AbstractActivity;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.TaskException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
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
            if (function == null) {
                throw new ConfigurationException("Function is null");
            }
        } catch (Exception e) {
            throw new ActivityException(this, "Cannot resolve function '" + functionName + "' from repository", e);
        }

        Object funcIn;
        try {
            funcIn = JSONUtil.treeToValue(input, function.getInputType(), context);
        } catch (JsonProcessingException e) {
            throw new ActivityException(this, "Exception while converting input for function '" + functionName + "'. Input = " + input + ". InputType = " + function.getInputType(), e);
        }

        Object output;
        try {
            output = function.exec(funcIn, new TaskContext(context.getExecutionId(), context.getProps(), context.getRepo(), context.getLogger()));
        } catch (TaskException e) {
            throw new ActivityException(this, e);
        }
        return SimpleOutboundMessage.of(JSONUtil.valueToTree(output, context));
    }
}
