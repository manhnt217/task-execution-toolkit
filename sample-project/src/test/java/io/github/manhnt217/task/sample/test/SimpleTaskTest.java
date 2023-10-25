package io.github.manhnt217.task.sample.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.sample.Util;
import io.github.manhnt217.task.sample.plugin.CurlTask;
import io.github.manhnt217.task.task_engine.exception.ActivityException;
import io.github.manhnt217.task.task_engine.activity.impl.DefaultActivityLogger;
import io.github.manhnt217.task.task_engine.activity.impl.TaskBasedActivity;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author manhnguyen
 */
public class SimpleTaskTest {

    @Test
    public void testSimple1() throws ActivityException, JsonProcessingException {
        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        Map<String, Object> input = ImmutableMap.of(
                "address", "https://example.com",
                "http-method", "GET"
        );


        TaskBasedActivity task = new TaskBasedActivity("simpleTask");
        task.setTask(TestUtil.loadTask("CurlTask"));
        task.setInputMapping("{\"url\": ._PROPS_.address, \"method\": ._PROPS_.\"http-method\"}");

        String execId = UUID.randomUUID().toString();
        JsonNode output = TestUtil.executeActivity(task, Util.OM.valueToTree(input), logHandler, execId);

        CurlTask.Output out = Util.OM.treeToValue(output, CurlTask.Output.class);
        assertEquals(200, out.getStatusCode());
    }
}
