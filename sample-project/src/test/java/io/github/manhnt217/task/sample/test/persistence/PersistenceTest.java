package io.github.manhnt217.task.sample.test.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.sample.JsonBasedTaskResolver;
import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.sample.Util;
import io.github.manhnt217.task.sample.plugin.AddTwoNumberTask;
import io.github.manhnt217.task.task_engine.activity.DefaultActivityLogger;
import io.github.manhnt217.task.task_engine.exception.TaskException;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author manhnguyen
 */
public class PersistenceTest {

    @Test
    public void testLoadAndExecuteSimpleTask() throws IOException, ConfigurationException, TaskException {
        JsonBasedTaskResolver taskRepo = new JsonBasedTaskResolver("simpleTaskRepo.json");

        JsonNode input = Util.OM.valueToTree(ImmutableMap.of(
                "a", 22,
                "b", 20
        ));

        DefaultActivityLogger logHandler = new DefaultActivityLogger();
        String execId = UUID.randomUUID().toString();

        JsonNode output = TestUtil.executeTask(AddTwoNumberTask.class.getName(), null, input, logHandler, execId, taskRepo);

        Integer result = Util.OM.treeToValue(output, Integer.class);
        assertEquals(42, result);
    }

    @Test
    public void testLoadAndExecuteCompositeTask() throws IOException, ConfigurationException, TaskException {
        JsonBasedTaskResolver taskRepo = new JsonBasedTaskResolver("complexTaskRepo.json");

        JsonNode input1 = Util.OM.valueToTree(ImmutableMap.of(
                "a", 22,
                "b", 20
        ));

        DefaultActivityLogger logHandler1 = new DefaultActivityLogger();
        Integer result1 = Util.OM.treeToValue(
                TestUtil.executeTask("c1", null, input1, logHandler1, UUID.randomUUID().toString(), taskRepo), Integer.class);
        assertThat(result1, is(42));
        assertThat(logHandler1.getLogs(), hasSize(1));
        assertThat(logHandler1.getLogs().get(0).getContent(), is("The result of Add task is: 42"));

        JsonNode input2 = Util.OM.valueToTree(ImmutableMap.of(
                "a", 22222,
                "b", 20
        ));

        DefaultActivityLogger logHandler2 = new DefaultActivityLogger();
        Integer result2 = Util.OM.treeToValue(
                TestUtil.executeTask("c1", null, input2, logHandler2, UUID.randomUUID().toString(), taskRepo), Integer.class);
        assertThat(result2, is(22242));
        assertThat(logHandler2.getLogs(), hasSize(0));

    }
}
