package io.github.manhnt217.task.sample.test.group;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.sample.LinearCompositeTask;
import io.github.manhnt217.task.sample.LinearGroupActivity;
import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.sample.plugin.AddTwoNumberTask;
import io.github.manhnt217.task.sample.plugin.CurlTask;
import io.github.manhnt217.task.sample.plugin.LogTask;
import io.github.manhnt217.task.task_engine.activity.DefaultActivityLogger;
import io.github.manhnt217.task.task_engine.activity.ExecutionLog;
import io.github.manhnt217.task.task_engine.activity.group.GroupActivity;
import io.github.manhnt217.task.task_engine.activity.task.TaskBasedActivity;
import io.github.manhnt217.task.task_engine.context.ActivityContext;
import io.github.manhnt217.task.task_engine.exception.TaskException;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import io.github.manhnt217.task.task_engine.persistence.builder.ActivityBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static io.github.manhnt217.task.sample.TestUtil.*;
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
    public void testGroupSimple() throws ConfigurationException, TaskException, IOException {
        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        TaskBasedActivity task1 = ActivityBuilder
                .task("task1")
                .taskName(LogTask.class.getName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": .START.task1Log}")
                .build();

        TaskBasedActivity task2 = ActivityBuilder
                .task("task2")
                .taskName(LogTask.class.getName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": .g1Start.START.task2Log}")
                .build();

        GroupActivity group1 = ActivityBuilder
                .group()
                .name("g1")
                .inputMapping(ActivityContext.ALL_SUBTASKS_JSLT)
                .start("g1Start")
                .end("g1End")
                .linkFromStart(task1)
                .link(task1, task2)
                .linkToEnd(task2)
                .build();

        JsonNode input = OM.valueToTree(ImmutableMap.of(
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

        TaskBasedActivity task1 = ActivityBuilder
                .task("taskA")
                .taskName(CurlTask.class.getName())
                .inputMapping("{\"method\": \"GET\",\"url\": \"example.com\"}")
                .build();

        assertDoesNotThrow(() -> new LinearGroupActivity("g1",
                "g1Start", "g1End",
                ActivityContext.ALL_SUBTASKS_JSLT,
                Arrays.asList(task1)));

        assertThrows(ConfigurationException.class, () -> new LinearGroupActivity("g2",
                "g2Start", "g2End",
                ActivityContext.ALL_SUBTASKS_JSLT,
                Arrays.asList(task1)));
    }

    /**
     * <img src="{@docRoot}/doc-files/images/testNameConflict.png">
     *
     * @throws ConfigurationException
     */
    @Test
    public void testNameConflictInsideAndOutsideGroup() throws ConfigurationException, JsonProcessingException {
        final String TASK_NAME = "taskA";

        TaskBasedActivity task1 = ActivityBuilder
                .task(TASK_NAME)
                .taskName(AddTwoNumberTask.class.getName())
                .inputMapping(OM.writeValueAsString(ImmutableMap.of("a", 1, "b", 2)))
                .build();

        TaskBasedActivity task2 = ActivityBuilder
                .task(TASK_NAME)
                .taskName(AddTwoNumberTask.class.getName())
                .inputMapping(OM.writeValueAsString(ImmutableMap.of("a", 1, "b", 2)))
                .build();

        GroupActivity group2 = new LinearGroupActivity("g2",
                "g2Start", "g2End",
                ActivityContext.ALL_SUBTASKS_JSLT,
                Arrays.asList(task2));

        assertThrows(ConfigurationException.class, () ->
                new LinearCompositeTask("c1", Arrays.asList(task1, group2)));
    }
}
