package io.github.manhnt217.task.sample;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.manhnt217.task.task_engine.activity.DefaultActivityLogger;
import io.github.manhnt217.task.task_engine.context.SimpleActivityContext;
import io.github.manhnt217.task.task_engine.exception.TaskException;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import io.github.manhnt217.task.task_engine.task.Task;
import io.github.manhnt217.task.task_engine.task.TaskResolver;

import java.io.IOException;

/**
 * @author manhnguyen
 */
public class TestUtil {

    public static final ObjectMapper OM = new ObjectMapper();

    static {
        OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OM.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static JsonNode executeTask(Task task, JsonNode props, JsonNode input, DefaultActivityLogger logger, String executionId) throws TaskException, ConfigurationException, IOException {
        SimpleActivityContext context = new SimpleActivityContext(executionId, props, new JsonBasedTaskResolver("builtinTaskRepo.json"));
        return task.run(input, "doesntmatter", logger, context);
    }

    public static JsonNode executeTask(String taskName, JsonNode props, JsonNode input, DefaultActivityLogger logger, String executionId, TaskResolver taskResolver) throws TaskException {
        Task task = taskResolver.resolve(taskName);
        SimpleActivityContext context = new SimpleActivityContext(executionId, props, taskResolver);
        return task.run(input, "doesntmatter", logger, context);
    }
}
