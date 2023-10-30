package io.github.manhnt217.task.sample.test.group;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.sample.LinearCompositeTask;
import io.github.manhnt217.task.sample.LinearGroupActivity;
import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.task_engine.activity.DefaultActivityLogger;
import io.github.manhnt217.task.task_engine.activity.ExecutionLog;
import io.github.manhnt217.task.task_engine.activity.group.Group;
import io.github.manhnt217.task.task_engine.activity.group.GroupActivity;
import io.github.manhnt217.task.task_engine.activity.simple.EndActivity;
import io.github.manhnt217.task.task_engine.activity.simple.StartActivity;
import io.github.manhnt217.task.task_engine.activity.task.TaskBasedActivity;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.exception.TaskException;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author manhnguyen
 */
public class GroupActivityTest {

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

        Group group = new Group();
        group.addActivity(new StartActivity("g1Start"));
        group.addActivity(new EndActivity("g1End"));
        group.addActivity(task1);
        group.addActivity(task2);

        group.linkFromStart(task1, null);
        group.linkActivities(task1, task2, null);
        group.linkToEnd(task2, null);

        GroupActivity group1 = new GroupActivity("g1", group);
        group1.setInputMapping(ActivityContext.ALL_SUBTASKS_JSLT);

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

        assertDoesNotThrow(() -> new LinearGroupActivity("g1",
                "g1Start", "g1End",
                ActivityContext.ALL_SUBTASKS_JSLT,
                Arrays.asList(task1)));

        assertThrows(ConfigurationException.class, () -> new LinearGroupActivity("g2",
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

        GroupActivity group2 = new LinearGroupActivity("g2",
                "g2Start", "g2End",
                ActivityContext.ALL_SUBTASKS_JSLT,
                Arrays.asList(task2));

        LinearCompositeTask task = new LinearCompositeTask("c1", Arrays.asList(task1, group2));

        assertDoesNotThrow(() ->
                TestUtil.executeTask(task, null, null, logHandler, UUID.randomUUID().toString()));
    }
}
