package io.github.manhnt217.task.core.activity.func;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.AbstractActivity;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.task.TaskContext;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.TaskException;
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

    @Override
    public OutboundMessage process(InboundMessage in, ActivityContext context) throws ActivityException {
        JsonNode input = in.getContent();
        try {
            Function function = context.getRepo().getFunction(functionName);
            if (function == null) {
                throw new TaskException(functionName, "Function not found");
            }
            JsonNode output = function.call(input, new TaskContext(context));
            return SimpleOutboundMessage.of(output);
        } catch (TaskException e) {
            throw new ActivityException(this, e);
        }
    }
}
