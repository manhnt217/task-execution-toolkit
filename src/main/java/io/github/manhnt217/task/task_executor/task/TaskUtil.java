package io.github.manhnt217.task.task_executor.task;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.process.Logger;
import org.apache.commons.lang3.StringUtils;

public class TaskUtil {
    public static JsonNode executeTask(Task task, ParamContext context, String executionSessionId, Logger logger) throws TaskExecutionException {
        JsonNode inputAfterTransform;
        try {
            inputAfterTransform = context.transformInput(task);
        } catch (Exception e) {
            throw new TaskExecutionException("Exception while transform the input", task, e);
        }

        log(task, task.getStartLogExpression(), context, executionSessionId, logger);

        JsonNode output = task.execute(inputAfterTransform, executionSessionId, logger);

        context.saveOutput(task, output);
        log(task, task.getEndLogExpression(), context, executionSessionId, logger);

        return output;
    }

    private static void log(Task task, String logExp, ParamContext ctx, String executionSessionId, Logger logger) {
        if (StringUtils.isBlank(logExp)) {
            return;
        }
        try {
            JsonNode jsonNode = ctx.transform(logExp);
            logger.info(executionSessionId, task.getName(), jsonNode.isContainerNode() ? "" : jsonNode.asText());
        } catch (Exception e) {
            logger.warn(executionSessionId, task.getName(), "Error while applying log expression to the context. Expression = " + logExp, e);
        }
    }
}
