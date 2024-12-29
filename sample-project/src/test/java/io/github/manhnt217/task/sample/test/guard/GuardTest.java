package io.github.manhnt217.task.sample.test.guard;

import io.github.manhnt217.task.core.activity.DefaultTaskLogger;
import io.github.manhnt217.task.core.activity.ExecutionLog;
import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.exception.TaskException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.task.function.Function;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.sample.plugin.Log;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static io.github.manhnt217.task.core.task.function.Function.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author manh nguyen
 */
public class GuardTest {

    /**
     * <img src="{@docRoot}/doc-files/images/testSimpleGuard.png">
     */
    @Test
    public void testSimpleGuard() throws ConfigurationException, TaskException, IOException {
        DefaultTaskLogger logHandler = new DefaultTaskLogger();

        PluginActivity p1 = ActivityBuilder
                .plugin("p1", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"p1\"}")
                .build();

        PluginActivity p2 = ActivityBuilder
                .plugin("p2", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"p2\"}")
                .build();

        Function func = ActivityBuilder
                .function("c1")
                .linkFromStart(p1, "3 > 5")
                .linkFromStart(p2, "10 - 3 == 7")
                .linkToEnd(p1)
                .linkToEnd(p2)
                .build();

        TestUtil.executeFunc(func, null, null, logHandler, UUID.randomUUID().toString());
        List<ExecutionLog> logs = logHandler.getLogs();

        assertThat(logs.size(), is(1));
        assertThat(logs.get(0).getContent(), is("p2"));
    }

    /**
     * <img src="{@docRoot}/doc-files/images/testOtherwise.png">
     */
    @Test
    public void testOtherwise() throws ConfigurationException, TaskException, IOException {
        DefaultTaskLogger logHandler = new DefaultTaskLogger();

        PluginActivity task1 = ActivityBuilder
                .plugin("task1", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"task1\"}")
                .build();

        PluginActivity task2 = ActivityBuilder
                .plugin("task2", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"task2\"}")
                .build();

        PluginActivity task3 = ActivityBuilder
                .plugin("task3", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"task3\"}")
                .build();

        Function compositeTask = ActivityBuilder
                .function("c1")
                .linkFromStart(task1, "3 > 5")
                .linkFromStart(task2, Group.OTHERWISE_GUARD_EXP)
                .linkFromStart(task3, "3 == 3")
                .linkToEnd(task1, null)
                .linkToEnd(task2, null)
                .linkToEnd(task3, null)
                .build();

        TestUtil.executeFunc(compositeTask, null, null, logHandler, UUID.randomUUID().toString());
        List<ExecutionLog> logs = logHandler.getLogs();

        assertThat(logs.size(), is(1));
        assertThat(logs.get(0).getContent(), is("task3"));
    }

    @Test
    public void testConflictedGuards() {

        PluginActivity p1 = ActivityBuilder
                .plugin("p1", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"p1\"}")
                .build();

        PluginActivity p2 = ActivityBuilder
                .plugin("p2", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"p2\"}")
                .build();

        ConfigurationException ex = assertThrows(ConfigurationException.class,
                () -> ActivityBuilder
                        .function("c1")
                        .linkFromStart(p1, "3 > 5")
                        .linkFromStart(p2, "3 > 5")
                        .linkToEnd(p1, null)
                        .linkToEnd(p2, null)
                        .build()
        );
        assertThat(ex.getMessage(), is("Configuration failed. Message = Guard '3 > 5' already been added for activity '" + START_ACTIVITY_NAME + "'"));
    }

    @Test
    public void testNoTrueGuard() throws ConfigurationException {
        DefaultTaskLogger logHandler = new DefaultTaskLogger();

        PluginActivity p1 = ActivityBuilder
                .plugin("p1", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"p1\"}")
                .build();

        PluginActivity p2 = ActivityBuilder
                .plugin("p2", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"p2\"}")
                .build();

        Function func = ActivityBuilder
                .function("c1")
                .linkFromStart(p1, "3 > 5")
                .linkFromStart(p2, "10 / 7 == 1")
                .linkToEnd(p1, null)
                .linkToEnd(p2, null)
                .build();

        assertThrows(TaskException.class, () ->
                TestUtil.executeFunc(func, null, null, logHandler, UUID.randomUUID().toString()));
    }
}
