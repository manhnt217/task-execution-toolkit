package io.github.manhnt217.task.sample.test.guard;

import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.task_engine.activity.simple.EndActivity;
import io.github.manhnt217.task.task_engine.activity.simple.StartActivity;
import io.github.manhnt217.task.task_engine.exception.TaskException;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import io.github.manhnt217.task.task_engine.activity.DefaultActivityLogger;
import io.github.manhnt217.task.task_engine.activity.ExecutionLog;
import io.github.manhnt217.task.task_engine.activity.group.Group;
import io.github.manhnt217.task.task_engine.activity.task.TaskBasedActivity;
import io.github.manhnt217.task.task_engine.task.CompositeTask;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static io.github.manhnt217.task.task_engine.task.CompositeTask.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author manhnguyen
 */
public class GuardTest {

    /**
     * <img src="{@docRoot}/doc-files/images/testSimpleGuard.png">
     *
     */
    @Test
    public void testSimpleGuard() throws ConfigurationException, TaskException {
        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        TaskBasedActivity task1 = new TaskBasedActivity("task1");
        task1.setInputMapping("{\"severity\": \"INFO\",\"message\": \"task1\"}");
        task1.setTask(TestUtil.loadTask("LogTask"));

        TaskBasedActivity task2 = new TaskBasedActivity("task2");
        task2.setInputMapping("{\"severity\": \"INFO\",\"message\": \"task2\"}");
        task2.setTask(TestUtil.loadTask("LogTask"));

        Group group = new Group();
        group.addActivity(new StartActivity(START_ACTIVITY_NAME));
        group.addActivity(new EndActivity(END_ACTIVITY_NAME));

        group.addActivity(task1);
        group.addActivity(task2);

        group.linkFromStart(task1, "3 > 5");
        group.linkFromStart(task2, "10 - 3 == 7");

        group.linkToEnd(task1, null);
        group.linkToEnd(task2, null);

        CompositeTask compositeTask = new CompositeTask("c1", group);

        TestUtil.executeTask(compositeTask, null, null, logHandler, UUID.randomUUID().toString());
        List<ExecutionLog> logs = logHandler.getLogs();

        assertThat(logs.size(), is(1));
        assertThat(logs.get(0).getContent(), is("task2"));
    }

    /**
     * <img src="{@docRoot}/doc-files/images/testOtherwise.png">
     *
     */
    @Test
    public void testOtherwise() throws ConfigurationException, TaskException {
        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        TaskBasedActivity task1 = new TaskBasedActivity("task1");
        task1.setInputMapping("{\"severity\": \"INFO\",\"message\": \"task1\"}");
        task1.setTask(TestUtil.loadTask("LogTask"));

        TaskBasedActivity task2 = new TaskBasedActivity("task2");
        task2.setInputMapping("{\"severity\": \"INFO\",\"message\": \"task2\"}");
        task2.setTask(TestUtil.loadTask("LogTask"));

        TaskBasedActivity task3 = new TaskBasedActivity("task3");
        task3.setInputMapping("{\"severity\": \"INFO\",\"message\": \"task3\"}");
        task3.setTask(TestUtil.loadTask("LogTask"));

        Group group = new Group();
        group.addActivity(new StartActivity(START_ACTIVITY_NAME));
        group.addActivity(new EndActivity(END_ACTIVITY_NAME));

        group.addActivity(task1);
        group.addActivity(task2);
        group.addActivity(task3);

        group.linkFromStart(task1, "3 > 5");
        group.linkFromStart(task2, Group.OTHERWISE_GUARD_EXP);
        group.linkFromStart(task3, "3 == 3");

        group.linkToEnd(task1, null);
        group.linkToEnd(task2, null);
        group.linkToEnd(task3, null);

        CompositeTask compositeTask = new CompositeTask("c1", group);

        TestUtil.executeTask(compositeTask, null, null, logHandler, UUID.randomUUID().toString());
        List<ExecutionLog> logs = logHandler.getLogs();

        assertThat(logs.size(), is(1));
        assertThat(logs.get(0).getContent(), is("task3"));
    }

    @Test
    public void testConflictedGuards() throws ConfigurationException {
        TaskBasedActivity task1 = new TaskBasedActivity("task1");
        task1.setInputMapping("{\"severity\": \"INFO\",\"message\": \"task1\"}");
        task1.setTask(TestUtil.loadTask("LogTask"));

        TaskBasedActivity task2 = new TaskBasedActivity("task2");
        task2.setInputMapping("{\"severity\": \"INFO\",\"message\": \"task2\"}");
        task2.setTask(TestUtil.loadTask("LogTask"));

        Group group = new Group();
        group.addActivity(new StartActivity(START_ACTIVITY_NAME));
        group.addActivity(new EndActivity(END_ACTIVITY_NAME));

        group.addActivity(task1);
        group.addActivity(task2);

        assertDoesNotThrow(
                () -> group.linkFromStart(task1, "3 > 5")
        );

        ConfigurationException ex = assertThrows(ConfigurationException.class,
                () -> group.linkFromStart(task2, "3 > 5")
        );
        assertThat(ex.getMessage(), is("Configuration failed. Message = Guard '3 > 5' already been added for activity '" + START_ACTIVITY_NAME + "'"));
    }

    @Test
    public void testNoTrueGuard() throws ConfigurationException {
        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        TaskBasedActivity task1 = new TaskBasedActivity("task1");
        task1.setInputMapping("{\"severity\": \"INFO\",\"message\": \"task1\"}");
        task1.setTask(TestUtil.loadTask("LogTask"));

        TaskBasedActivity task2 = new TaskBasedActivity("task2");
        task2.setInputMapping("{\"severity\": \"INFO\",\"message\": \"task2\"}");
        task2.setTask(TestUtil.loadTask("LogTask"));

        Group group = new Group();
        group.addActivity(new StartActivity(START_ACTIVITY_NAME));
        group.addActivity(new EndActivity(END_ACTIVITY_NAME));

        group.addActivity(task1);
        group.addActivity(task2);

        group.linkFromStart(task1, "3 > 5");
        group.linkFromStart(task2, "10 / 7 == 1");

        CompositeTask compositeTask = new CompositeTask("c1", group);

        assertThrows(TaskException.class, () ->
                TestUtil.executeTask(compositeTask, null, null, logHandler, UUID.randomUUID().toString()));
    }
}
