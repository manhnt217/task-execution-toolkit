package io.github.manhnt217.task.sample.test.group;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.sample.LinearCompositeTask;
import io.github.manhnt217.task.sample.LinearGroup;
import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.exception.TaskException;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import io.github.manhnt217.task.task_engine.activity.impl.DefaultActivityLogger;
import io.github.manhnt217.task.task_engine.activity.impl.ExecutionLog;
import io.github.manhnt217.task.task_engine.activity.impl.group.Group;
import io.github.manhnt217.task.task_engine.activity.impl.task.TaskBasedActivity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author manhnguyen
 */
@Execution(ExecutionMode.CONCURRENT)
public class GroupTest {

    /**
     * <img src="{@docRoot}/doc-files/images/testGroup.png">
     *
     * @throws ConfigurationException
     */
    @Test
    public void testGroupSimple() throws ConfigurationException, TaskException {
        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        TaskBasedActivity task1 = new TaskBasedActivity("task1");
        task1.setInputMapping("{\"severity\": \"INFO\",\"message\": .START.task1Log}");
        task1.setTask(TestUtil.loadTask("LogTask"));

        TaskBasedActivity task2 = new TaskBasedActivity("task2");
        task2.setInputMapping("{\"severity\": \"INFO\",\"message\": .g1Start.START.task2Log}");
        task2.setTask(TestUtil.loadTask("LogTask"));

        Group group1 = new Group("g1", "g1Start", "g1End");
        group1.setInputMapping(ActivityContext.ALL_SUBTASKS_JSLT);
        group1.addActivity(task1);
        group1.addActivity(task2);

        group1.linkFromStart(task1, null);
        group1.linkActivities(task1, task2, null);
        group1.linkToEnd(task2);

        JsonNode input = TestUtil.OM.valueToTree(ImmutableMap.of(
                "task1Log", "log for task number 1",
                "task2Log", "log for task number 2"
        ));

        LinearCompositeTask task = new LinearCompositeTask("c1", Collections.singletonList(group1));

        TestUtil.executeTask(task, null, input, logHandler, UUID.randomUUID().toString());

        List<ExecutionLog> logs = logHandler.getLogs();

        assertThat(logs.size(), is(2));
        assertThat(logs.get(0).getContent(), is("log for task number 1"));
        assertThat(logs.get(1).getContent(), is("log for task number 2"));
    }

    @Test
    public void testAddActivityToTwoGroups() {
        TaskBasedActivity task1 = new TaskBasedActivity("taskA");
        task1.setInputMapping("{\"method\": \"GET\",\"url\": \"example.com\"}");
        task1.setTask(TestUtil.loadTask("CurlTask"));

        assertDoesNotThrow(() -> new LinearGroup("g1",
                "g1Start", "g1End",
                ActivityContext.ALL_SUBTASKS_JSLT,
                Arrays.asList(task1)));

        assertThrows(ConfigurationException.class, () -> new LinearGroup("g2",
                "g2Start", "g2End",
                ActivityContext.ALL_SUBTASKS_JSLT,
                Arrays.asList(task1)));
    }

    @Disabled("Will be fixed later")
    @Test
    public void testNameConflictInsideAndOutsideGroup() throws ConfigurationException {
        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        final String TASK_NAME = "taskA";
        TaskBasedActivity task1 = new TaskBasedActivity(TASK_NAME);
        task1.setInputMapping("{\"method\": \"GET\",\"url\": \"https://example.com\"}");
        task1.setTask(TestUtil.loadTask("CurlTask"));

        TaskBasedActivity task2 = new TaskBasedActivity(TASK_NAME);
        task2.setInputMapping("{\"method\": \"GET\",\"url\": \"https://example.com\"}");
        task2.setTask(TestUtil.loadTask("CurlTask"));

        Group group2 = new LinearGroup("g2",
                "g2Start", "g2End",
                ActivityContext.ALL_SUBTASKS_JSLT,
                Arrays.asList(task2));

        LinearCompositeTask task = new LinearCompositeTask("c1", Arrays.asList(task1, group2));

        assertDoesNotThrow(() ->
                TestUtil.executeTask(task, null, null, logHandler, UUID.randomUUID().toString()));
    }
}
