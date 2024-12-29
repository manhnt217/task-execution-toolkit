package io.github.manhnt217.task.sample.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.sample.Util;
import io.github.manhnt217.task.sample.plugin.CurlTask;
import io.github.manhnt217.task.core.activity.DefaultActivityLogger;
import io.github.manhnt217.task.core.exception.TaskException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.core.task.PluginTask;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author manhnguyen
 */
public class SimpleTaskTest {

    @Test
    public void testSimple1() throws IOException, TaskException, ConfigurationException {
        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        PluginTask task = ActivityBuilder.plugin(CurlTask.class.getName()).build();
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
