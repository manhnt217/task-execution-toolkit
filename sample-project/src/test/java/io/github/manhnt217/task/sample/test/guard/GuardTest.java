package io.github.manhnt217.task.sample.test.guard;

import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.task_engine.exception.ActivityException;
import io.github.manhnt217.task.task_engine.exception.TaskException;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import io.github.manhnt217.task.task_engine.activity.impl.DefaultActivityLogger;
import io.github.manhnt217.task.task_engine.activity.impl.ExecutionLog;
import io.github.manhnt217.task.task_engine.activity.impl.LinkBasedActivityGroup;
import io.github.manhnt217.task.task_engine.activity.impl.TaskBasedActivity;
import io.github.manhnt217.task.task_engine.task.CompositeTask;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

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


        CompositeTask compositeTask = new CompositeTask("c1");

        compositeTask.addActivity(task1);
        compositeTask.addActivity(task2);

        compositeTask.linkFromStart(task1, "3 > 5");
        compositeTask.linkFromStart(task2, "10 - 3 == 7");

        compositeTask.linkToEnd(task1);
        compositeTask.linkToEnd(task2);

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

        CompositeTask compositeTask = new CompositeTask("c1");

        compositeTask.addActivity(task1);
        compositeTask.addActivity(task2);
        compositeTask.addActivity(task3);

        compositeTask.linkFromStart(task1, "3 > 5");
        compositeTask.linkFromStart(task2, LinkBasedActivityGroup.OTHERWISE_GUARD_EXP);
        compositeTask.linkFromStart(task3, "3 == 3");

        compositeTask.linkToEnd(task1);
        compositeTask.linkToEnd(task2);
        compositeTask.linkToEnd(task3);

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


        CompositeTask compositeTask = new CompositeTask("c1");

        compositeTask.addActivity(task1);
        compositeTask.addActivity(task2);

        assertDoesNotThrow(
                () -> compositeTask.linkFromStart(task1, "3 > 5")
        );

        ConfigurationException ex = assertThrows(ConfigurationException.class,
                () -> compositeTask.linkFromStart(task2, "3 > 5")
        );
        assertThat(ex.getMessage(), is("Configuration failed. Message = Guard '3 > 5' already been added for activity '" + CompositeTask.START_ACTIVITY_NAME + "'"));
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


        CompositeTask compositeTask = new CompositeTask("c1");

        compositeTask.addActivity(task1);
        compositeTask.addActivity(task2);

        compositeTask.linkFromStart(task1, "3 > 5");
        compositeTask.linkFromStart(task2, "10 / 7 == 1");

        assertThrows(TaskException.class, () ->
                TestUtil.executeTask(compositeTask, null, null, logHandler, UUID.randomUUID().toString()));
    }
}