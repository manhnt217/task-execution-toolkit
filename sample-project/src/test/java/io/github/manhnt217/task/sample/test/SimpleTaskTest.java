package io.github.manhnt217.task.sample.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.sample.Util;
import io.github.manhnt217.task.sample.plugin.CurlTask;
import io.github.manhnt217.task.task_engine.activity.impl.DefaultActivityLogger;
import io.github.manhnt217.task.task_engine.exception.TaskException;
import io.github.manhnt217.task.task_engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author manhnguyen
 */
public class SimpleTaskTest {

    @Test
    public void testSimple1() throws JsonProcessingException, TaskException {
        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        Task task = TestUtil.loadTask("CurlTask");
        String execId = UUID.randomUUID().toString();

        JsonNode input = Util.OM.valueToTree(ImmutableMap.of(
                "url", "https://example.com",
                "method", "GET"
        ));
        JsonNode output = TestUtil.executeTask(task, null, input, logHandler, execId);

        CurlTask.Output out = Util.OM.treeToValue(output, CurlTask.Output.class);
        assertEquals(200, out.getStatusCode());
    }
}
