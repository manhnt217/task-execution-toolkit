package io.github.manhnt217.task.sample.test.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.sample.Util;
import io.github.manhnt217.task.task_engine.activity.DefaultActivityLogger;
import io.github.manhnt217.task.task_engine.exception.TaskException;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import io.github.manhnt217.task.task_engine.persistence.model.TaskDto;
import io.github.manhnt217.task.task_engine.persistence.service.TaskService;
import io.github.manhnt217.task.task_engine.task.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
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
        InputStream resourceAsStream = PersistenceTest.class.getClassLoader().getResourceAsStream("simpleTask.json");
        TaskDto taskDto = TestUtil.OM.readValue(resourceAsStream, TaskDto.class);
        Task task = TaskService.buildTask(taskDto);

        JsonNode input = Util.OM.valueToTree(ImmutableMap.of(
                "a", 22,
                "b", 20
        ));

        DefaultActivityLogger logHandler = new DefaultActivityLogger();
        String execId = UUID.randomUUID().toString();

        JsonNode output = TestUtil.executeTask(task, null, input, logHandler, execId);

        Integer result =  Util.OM.treeToValue(output, Integer.class);
        assertEquals(42, result);
    }

    @Test
    public void testLoadAndExecuteCompositeTask() throws IOException, ConfigurationException, TaskException {
        InputStream resourceAsStream = PersistenceTest.class.getClassLoader().getResourceAsStream("complexTask.json");
        TaskDto taskDto = TestUtil.OM.readValue(resourceAsStream, TaskDto.class);
        Task task = TaskService.buildTask(taskDto);

        JsonNode input1 = Util.OM.valueToTree(ImmutableMap.of(
                "a", 22,
                "b", 20
        ));

        DefaultActivityLogger logHandler1 = new DefaultActivityLogger();
        Integer result1 =  Util.OM.treeToValue(
                TestUtil.executeTask(task, null, input1, logHandler1, UUID.randomUUID().toString()), Integer.class);
        assertThat(result1, is(42));
        assertThat(logHandler1.getLogs(), hasSize(1));
        assertThat(logHandler1.getLogs().get(0).getContent(), is("The result of Add task is: 42"));

        JsonNode input2 = Util.OM.valueToTree(ImmutableMap.of(
                "a", 22222,
                "b", 20
        ));

        DefaultActivityLogger logHandler2 = new DefaultActivityLogger();
        Integer result2 =  Util.OM.treeToValue(
                TestUtil.executeTask(task, null, input2, logHandler2, UUID.randomUUID().toString()), Integer.class);
        assertThat(result2, is(22242));
        assertThat(logHandler2.getLogs(), hasSize(0));

    }
}
