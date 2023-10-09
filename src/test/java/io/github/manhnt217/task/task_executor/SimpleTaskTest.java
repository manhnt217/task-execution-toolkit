package io.github.manhnt217.task.task_executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.task_executor.executor.TaskExecutionException;
import io.github.manhnt217.task.task_executor.process.DefaultLogger;
import io.github.manhnt217.task.task_executor.process.builtin.CurlTemplate;
import io.github.manhnt217.task.task_executor.task.TemplateTask;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static io.github.manhnt217.task.task_executor.common.CommonUtil.OM;
import static org.junit.jupiter.api.Assertions.*;

public class SimpleTaskTest {

    @Test
    public void testSimple1() throws TaskExecutionException, JsonProcessingException {
        DefaultLogger logHandler = new DefaultLogger();

        Map<String, Object> input = ImmutableMap.of(
                "address", "https://example.com",
                "http-method", "GET"
        );

        TemplateTask task = new TemplateTask();
        task.setTaskName("simpleTask");
        task.setTemplateName("CurlTemplate");
        task.setEndLogExpression("\"Finish task 1\"");
        task.setInputMappingExpression("{\"url\": ._PARENT_.address, \"method\": ._PARENT_.\"http-method\"}");

        String execId = UUID.randomUUID().toString();
        JsonNode output = TestUtil.executeTask(task, OM.valueToTree(input), logHandler, execId);

        CurlTemplate.Output out = OM.treeToValue(output, CurlTemplate.Output.class);
        assertEquals(200, out.getStatusCode());
    }
}
