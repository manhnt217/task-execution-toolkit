package io.github.manhnt217.task.sample.test.guard;

import io.github.manhnt217.task.core.activity.TaskLogger;
import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.repo.EngineRepository;
import io.github.manhnt217.task.core.task.RootContext;
import io.github.manhnt217.task.core.task.TaskException;
import io.github.manhnt217.task.core.task.function.Function;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.plugin.Log;
import io.github.manhnt217.task.sample.test.AbstractEngineTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static io.github.manhnt217.task.core.task.function.Function.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

/**
 * @author manh nguyen
 */
@ExtendWith(MockitoExtension.class)
public class GuardTest extends AbstractEngineTest {

    /**
     * <img src="{@docRoot}/doc-files/images/testSimpleGuard.png">
     */
    @Test
    public void testSimpleGuard() throws ConfigurationException, TaskException, IOException, ActivityException {

        PluginActivity p1 = ActivityBuilder
                .plugin("p1", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"p1\"}")
                .build();

        PluginActivity p2 = ActivityBuilder
                .plugin("p2", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"p2\"}")
                .build();

        Function<Void, Void> func = ActivityBuilder
                .routine("c1")
                .linkFromStart(p1, "3 > 5")
                .linkFromStart(p2, "10 - 3 == 7")
                .linkToEnd(p1)
                .linkToEnd(p2)
                .build();

        RootContext context = new RootContext(null, repo, logger);
        func.exec(null, context);

        verify(logger).info(any(), any(), any(), eq("p2"));
    }

    /**
     * <img src="{@docRoot}/doc-files/images/testOtherwise.png">
     */
    @Test
    public void testOtherwise() throws ConfigurationException, TaskException, IOException, ActivityException {
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

        Function<Void, Void> func = ActivityBuilder
                .routine("c1")
                .linkFromStart(task1, "3 > 5")
                .linkFromStart(task2, Group.OTHERWISE_GUARD_EXP)
                .linkFromStart(task3, "3 == 3")
                .linkToEnd(task1, null)
                .linkToEnd(task2, null)
                .linkToEnd(task3, null)
                .build();

        func.exec(null, new RootContext(null, repo, logger));
        verify(logger).info(any(), any(), any(), eq("task3"));
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
                        .routine("c1")
                        .linkFromStart(p1, "3 > 5")
                        .linkFromStart(p2, "3 > 5")
                        .linkToEnd(p1, null)
                        .linkToEnd(p2, null)
                        .build()
        );
        assertThat(ex.getMessage(), is("Configuration failed. Message = Guard '3 > 5' already been added for activity '" + START_ACTIVITY_NAME + "'"));
    }

    @Test
    public void testNoTrueGuard(@Mock EngineRepository repo, @Mock TaskLogger logger) throws ConfigurationException {
        PluginActivity p1 = ActivityBuilder
                .plugin("p1", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"p1\"}")
                .build();

        PluginActivity p2 = ActivityBuilder
                .plugin("p2", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"p2\"}")
                .build();

        Function<Void, Void> func = ActivityBuilder
                .routine("c1")
                .linkFromStart(p1, "3 > 5")
                .linkFromStart(p2, "10 / 7 == 1")
                .linkToEnd(p1, null)
                .linkToEnd(p2, null)
                .build();

        assertThrows(ActivityException.class, () -> func.exec(null, new RootContext(null, repo, logger)));
    }
}
