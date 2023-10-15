package io.github.manhnt217.task.task_executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.task_executor.activity.ActivityException;
import io.github.manhnt217.task.task_executor.activity.impl.DefaultLogger;
import io.github.manhnt217.task.task_executor.template.builtin.CurlTemplate;
import io.github.manhnt217.task.task_executor.activity.impl.task.TemplateTask;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static io.github.manhnt217.task.task_executor.common.CommonUtil.OM;
import static org.junit.jupiter.api.Assertions.*;

public class SimpleTaskTest {

    @Test
    public void testSimple1() throws ActivityException, JsonProcessingException {
        DefaultLogger logHandler = new DefaultLogger();

        Map<String, Object> input = ImmutableMap.of(
                "address", "https://example.com",
                "http-method", "GET"
        );

        TemplateTask task = new TemplateTask("simpleTask");
        task.setTemplateName("CurlTemplate");
        task.setEndLog("\"Finish task 1\"");
        task.setInputMapping("{\"url\": ._PROPS_.address, \"method\": ._PROPS_.\"http-method\"}");

        String execId = UUID.randomUUID().toString();
        JsonNode output = TestUtil.executeTask(task, OM.valueToTree(input), logHandler, execId);

        CurlTemplate.Output out = OM.treeToValue(output, CurlTemplate.Output.class);
        assertEquals(200, out.getStatusCode());
    }
}
