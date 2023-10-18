package io.github.manhnt217.task.task_executor.group;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.task_executor.LinearCompositeTask;
import io.github.manhnt217.task.task_executor.TestUtil;
import io.github.manhnt217.task.task_executor.activity.ActivityException;
import io.github.manhnt217.task.task_executor.activity.ConfigurationException;
import io.github.manhnt217.task.task_executor.activity.impl.DefaultActivityLogger;
import io.github.manhnt217.task.task_executor.activity.impl.ExecutionLog;
import io.github.manhnt217.task.task_executor.activity.impl.Group;
import io.github.manhnt217.task.task_executor.activity.impl.TaskBasedActivity;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static io.github.manhnt217.task.task_executor.context.ActivityContext.OBJECT_MAPPER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author manhnguyen
 */
public class GroupTest {

    @Test
    public void testGroupSimple() throws ActivityException, ConfigurationException {
        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        TaskBasedActivity task1 = new TaskBasedActivity("task1");
        task1.setInputMapping("{\"severity\": \"INFO\",\"message\": .START.task1Log}");
        task1.setTask(TestUtil.loadTask("LogTask"));

        TaskBasedActivity task2 = new TaskBasedActivity("task2");
        task2.setInputMapping("{\"severity\": \"INFO\",\"message\": .g1Start.START.task2Log}");
        task2.setTask(TestUtil.loadTask("LogTask"));

        Group group1 = new Group("g1", "g1Start", "g1End");
        group1.addActivity(task1);
        group1.addActivity(task2);

        group1.linkFromStart(task1, null);
        group1.linkActivities(task1, task2, null);
        group1.linkToEnd(task2);

        JsonNode props = OBJECT_MAPPER.valueToTree(ImmutableMap.of(
                "log", "log for task number "
        ));

        TaskBasedActivity complexTask = new TaskBasedActivity(
                "complexTask",
                new LinearCompositeTask("c1", Collections.singletonList(group1)));

        /** This will produce
         * {
         *  "task1Log" : "log for task number 1",
         *  "task2Log" : "log for task number 2"
         * }
         * for the input
         */
        complexTask.setInputMapping("zip((._PROPS_.log | [.,.]), [1, 2]) | {for(.) \"task\" + string(.[1]) + \"Log\" : .[0] + .[1]}");

        TestUtil.executeActivity(complexTask, props, logHandler, UUID.randomUUID().toString());

        List<ExecutionLog> logs = logHandler.getLogs();

        assertThat(logs.size(), is(2));
        assertThat(logs.get(0).getContent(), is("log for task number 1"));
        assertThat(logs.get(1).getContent(), is("log for task number 2"));
    }
}
