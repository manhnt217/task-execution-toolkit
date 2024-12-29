package io.github.manhnt217.task.task_executor;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.activity.ActivityException;
import io.github.manhnt217.task.task_executor.activity.OutboundMessage;
import io.github.manhnt217.task.task_executor.activity.impl.SimpleInboundMessage;
import io.github.manhnt217.task.task_executor.process.DefaultLogger;
import io.github.manhnt217.task.task_executor.task.context.ParamContext;
import io.github.manhnt217.task.task_executor.task.Task;
import io.github.manhnt217.task.task_executor.task.TaskExecutionException;

public class TestUtil {

    public static JsonNode executeTask(Task task, JsonNode input, DefaultLogger logger, String executionSessionId) throws ActivityException {

        ParamContext context = new ParamContext(input);

        JsonNode inputAfterTransform;
        try {
            inputAfterTransform = context.transformInput(task);
        } catch (Exception e) {
            throw new TaskExecutionException("Exception while transform the input", task, e);
        }

        OutboundMessage output = task.process(SimpleInboundMessage.of(inputAfterTransform), executionSessionId, logger, context);

        return output.getContent();
    }

}
