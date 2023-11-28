package io.github.manhnt217.task.sample;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.manhnt217.task.core.activity.DefaultTaskLogger;
import io.github.manhnt217.task.core.task.TaskContext;
import io.github.manhnt217.task.core.exception.TaskException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.repo.EngineRepository;
import io.github.manhnt217.task.core.task.function.Function;

import java.io.IOException;

/**
 * @author manh nguyen
 */
public class TestUtil {

    public static final ObjectMapper OM = new ObjectMapper();

    static {
        OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OM.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static JsonNode executeFunc(Function function, JsonNode props, JsonNode input, DefaultTaskLogger logger, String executionId) throws TaskException, ConfigurationException, IOException {
        TaskContext ctx = new TaskContext(executionId, props, new JsonBasedEngineRepository("builtinTaskRepo.json"), logger);
        return function.call(input, ctx);
    }

    public static JsonNode executeFunc(String taskName, JsonNode props, JsonNode input, DefaultTaskLogger logger, String executionId, EngineRepository repo) throws TaskException {
        Function function = repo.getFunction(taskName);
        TaskContext ctx = new TaskContext(executionId, props, repo, logger);
        return function.call(input, ctx);
    }
}