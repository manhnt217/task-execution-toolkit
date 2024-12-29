package io.github.manhnt217.task.sample.test.group;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.core.activity.group.GroupActivity;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.exception.TaskException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.task.TaskContext;
import io.github.manhnt217.task.core.task.function.Function;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.plugin.Log;
import io.github.manhnt217.task.sample.test.AbstractEngineTest;
import io.github.manhnt217.task.sample.test.example_plugin.AddTwoNumber;
import io.github.manhnt217.task.sample.test.example_plugin.Curl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static io.github.manhnt217.task.sample.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author manh nguyen
 */
@ExtendWith(MockitoExtension.class)
public class GroupActivityTest extends AbstractEngineTest {

    /**
     * <img src="{@docRoot}/doc-files/images/testGroup.png">
     *
     * @throws ConfigurationException
     */
    @Test
    public void testGroupSimple() throws ConfigurationException, TaskException {

        PluginActivity p1 = buildPluginActivity(
                "p1",
                Log.class.getSimpleName(),
                "{\"severity\": \"INFO\",\"message\": .START.p1Log}");

        PluginActivity p2 = buildPluginActivity(
                "p2", Log.class.getSimpleName(),
                "{\"severity\": \"INFO\",\"message\": .g1Start.START.p2Log}");

        GroupActivity group1 = buildLinearGroup(
                "g1", false,
                ActivityContext.ALL_SUBTASKS_JSLT,
                null,
                "g1Start",
                "g1End",
                p1, p2);

        Function<Map, Void> func = buildLinearFunc("c1", Map.class, Void.class, null, group1);

        TaskContext context = new TaskContext(func.getName(), null, repo, futureProcessor, logger);
        func.exec(ImmutableMap.of(
                "p1Log", "log for plugin number 1",
                "p2Log", "log for plugin number 2"
        ), context);

        verify(logger).info(any(), any(), any(), eq("log for plugin number 1"));
        verify(logger).info(any(), any(), any(), eq("log for plugin number 2"));
    }

    @Test
    public void testAddActivityToTwoGroups() {

        PluginActivity p1 = ActivityBuilder
                .plugin("taskA", Curl.class.getSimpleName())
                .inputMapping("{\"method\": \"GET\",\"url\": \"example.com\"}")
                .build();

        assertDoesNotThrow(() -> buildLinearGroup("g1", false,
                null,
                ActivityContext.ALL_SUBTASKS_JSLT, "g1Start",
                "g1End",
                p1));

        assertThrows(ConfigurationException.class, () -> buildLinearGroup("g2", false,
                null,
                ActivityContext.ALL_SUBTASKS_JSLT,
                "g2Start",
                "g2End",
                p1));
    }

    /**
     * <img src="{@docRoot}/doc-files/images/testNameConflict.png">
     *
     * @throws ConfigurationException
     */
    @Test
    public void testNameConflictInsideAndOutsideGroup() throws ConfigurationException, JsonProcessingException {
        final String TASK_NAME = "taskA";

        PluginActivity p1 = ActivityBuilder
                .plugin(TASK_NAME, AddTwoNumber.class.getSimpleName())
                .inputMapping(OM.writeValueAsString(ImmutableMap.of("a", 1, "b", 2)))
                .build();

        PluginActivity p2 = ActivityBuilder
                .plugin(TASK_NAME, AddTwoNumber.class.getSimpleName())
                .inputMapping(OM.writeValueAsString(ImmutableMap.of("a", 1, "b", 2)))
                .build();

        GroupActivity group2 = buildLinearGroup("g2", false,
                null,
                ActivityContext.ALL_SUBTASKS_JSLT,
                "g2Start",
                "g2End",
                p2);

        assertThrows(ConfigurationException.class, () ->
                buildLinearRoutine("c1", p1, group2));
    }
}
