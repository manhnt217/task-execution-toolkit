package io.github.manhnt217.task.task_executor;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.executor.CompoundTaskExecutor;
import io.github.manhnt217.task.task_executor.executor.ParamContext;
import io.github.manhnt217.task.task_executor.executor.TaskExecutionException;
import io.github.manhnt217.task.task_executor.process.DefaultLogger;
import io.github.manhnt217.task.task_executor.task.Task;

public class TestUtil {

    public static JsonNode executeTask(Task task, JsonNode input, DefaultLogger logger, String executionSessionId) throws TaskExecutionException {

        ParamContext context = new ParamContext();
        context.setParentInput(input);
        return CompoundTaskExecutor.executeTask(task, context, executionSessionId, logger);
    }
}
