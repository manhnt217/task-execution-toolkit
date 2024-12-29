package io.github.manhnt217.task.task_executor;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.process.DefaultLogger;
import io.github.manhnt217.task.task_executor.task.*;

public class TestUtil {

    public static JsonNode executeTask(Task task, JsonNode input, DefaultLogger logger, String executionSessionId) throws TaskExecutionException {

        ParamContext context = new ParamContext();
        context.setParentInput(input);
        return TaskUtil.executeTask(task, context, executionSessionId, logger);
    }
}
