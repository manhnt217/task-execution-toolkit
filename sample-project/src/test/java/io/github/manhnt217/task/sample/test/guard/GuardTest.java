package io.github.manhnt217.task.sample.test.guard;

import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.sample.plugin.LogTask;
import io.github.manhnt217.task.core.activity.DefaultActivityLogger;
import io.github.manhnt217.task.core.activity.ExecutionLog;
import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.activity.task.TaskBasedActivity;
import io.github.manhnt217.task.core.exception.TaskException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.core.task.CompositeTask;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static io.github.manhnt217.task.core.task.CompositeTask.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author manhnguyen
 */
public class GuardTest {

    /**
     * <img src="{@docRoot}/doc-files/images/testSimpleGuard.png">
     */
    @Test
    public void testSimpleGuard() throws ConfigurationException, TaskException, IOException {
        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        TaskBasedActivity task1 = ActivityBuilder
                .task("task1")
                .taskName(LogTask.class.getName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"task1\"}")
                .build();

        TaskBasedActivity task2 = ActivityBuilder
                .task("task2")
                .taskName(LogTask.class.getName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"task2\"}")
                .build();

        CompositeTask compositeTask = ActivityBuilder
                .composite("c1")
                .linkFromStart(task1, "3 > 5")
                .linkFromStart(task2, "10 - 3 == 7")
                .linkToEnd(task1)
                .linkToEnd(task2)
                .build();

        TestUtil.executeTask(compositeTask, null, null, logHandler, UUID.randomUUID().toString());
        List<ExecutionLog> logs = logHandler.getLogs();

        assertThat(logs.size(), is(1));
        assertThat(logs.get(0).getContent(), is("task2"));
    }

    /**
     * <img src="{@docRoot}/doc-files/images/testOtherwise.png">
     */
    @Test
    public void testOtherwise() throws ConfigurationException, TaskException, IOException {
        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        TaskBasedActivity task1 = ActivityBuilder
                .task("task1")
                .taskName(LogTask.class.getName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"task1\"}")
                .build();

        TaskBasedActivity task2 = ActivityBuilder
                .task("task2")
                .taskName(LogTask.class.getName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"task2\"}")
                .build();

        TaskBasedActivity task3 = ActivityBuilder
                .task("task3")
                .taskName(LogTask.class.getName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"task3\"}")
                .build();

        CompositeTask compositeTask = ActivityBuilder
                .composite("c1")
                .linkFromStart(task1, "3 > 5")
                .linkFromStart(task2, Group.OTHERWISE_GUARD_EXP)
                .linkFromStart(task3, "3 == 3")
                .linkToEnd(task1, null)
                .linkToEnd(task2, null)
                .linkToEnd(task3, null)
                .build();

        TestUtil.executeTask(compositeTask, null, null, logHandler, UUID.randomUUID().toString());
        List<ExecutionLog> logs = logHandler.getLogs();

        assertThat(logs.size(), is(1));
        assertThat(logs.get(0).getContent(), is("task3"));
    }

    @Test
    public void testConflictedGuards() {

        TaskBasedActivity task1 = ActivityBuilder
                .task("task1")
                .taskName(LogTask.class.getName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"task1\"}")
                .build();

        TaskBasedActivity task2 = ActivityBuilder
                .task("task2")
                .taskName(LogTask.class.getName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"task2\"}")
                .build();

        ConfigurationException ex = assertThrows(ConfigurationException.class,
                () -> ActivityBuilder
                        .composite("c1")
                        .linkFromStart(task1, "3 > 5")
                        .linkFromStart(task2, "3 > 5")
                        .linkToEnd(task1, null)
                        .linkToEnd(task2, null)
                        .build()
        );
        assertThat(ex.getMessage(), is("Configuration failed. Message = Guard '3 > 5' already been added for activity '" + START_ACTIVITY_NAME + "'"));
    }

    @Test
    public void testNoTrueGuard() throws ConfigurationException {
        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        TaskBasedActivity task1 = ActivityBuilder
                .task("task1")
                .taskName(LogTask.class.getName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"task1\"}")
                .build();

        TaskBasedActivity task2 = ActivityBuilder
                .task("task2")
                .taskName(LogTask.class.getName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"task2\"}")
                .build();

        CompositeTask compositeTask = ActivityBuilder
                .composite("c1")
                .linkFromStart(task1, "3 > 5")
                .linkFromStart(task2, "10 / 7 == 1")
                .linkToEnd(task1, null)
                .linkToEnd(task2, null)
                .build();

        assertThrows(TaskException.class, () ->
                TestUtil.executeTask(compositeTask, null, null, logHandler, UUID.randomUUID().toString()));
    }
}
