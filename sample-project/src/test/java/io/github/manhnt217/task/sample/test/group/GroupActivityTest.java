package io.github.manhnt217.task.sample.test.group;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.core.activity.TaskLogger;
import io.github.manhnt217.task.core.activity.group.GroupActivity;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.exception.TaskException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.repo.EngineRepository;
import io.github.manhnt217.task.core.task.TaskContext;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.sample.LinearFunction;
import io.github.manhnt217.task.sample.LinearGroupActivity;
import io.github.manhnt217.task.sample.plugin.AddTwoNumber;
import io.github.manhnt217.task.sample.plugin.Curl;
import io.github.manhnt217.task.sample.plugin.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static io.github.manhnt217.task.sample.TestUtil.OM;
import static io.github.manhnt217.task.sample.test.ComplexFunctionTest.mockBuiltInRepo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author manh nguyen
 */
@ExtendWith(MockitoExtension.class)
public class GroupActivityTest {

    /**
     * <img src="{@docRoot}/doc-files/images/testGroup.png">
     *
     * @throws ConfigurationException
     */
    @Test
    public void testGroupSimple(@Mock EngineRepository repo, @Mock TaskLogger logger) throws ConfigurationException, TaskException {
        mockBuiltInRepo(repo);

        PluginActivity p1 = ActivityBuilder
                .plugin("p1", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": .START.p1Log}")
                .build();

        PluginActivity p2 = ActivityBuilder
                .plugin("p2", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": .g1Start.START.p2Log}")
                .build();

        GroupActivity group1 = ActivityBuilder
                .group(false)
                .name("g1")
                .inputMapping(ActivityContext.ALL_SUBTASKS_JSLT)
                .start("g1Start")
                .end("g1End")
                .linkFromStart(p1)
                .link(p1, p2)
                .linkToEnd(p2)
                .build();

        LinearFunction<Object, Object> func = new LinearFunction<>("c1", Collections.singletonList(group1), Object.class, Object.class);

        TaskContext context = new TaskContext(null, repo, logger);
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

        assertDoesNotThrow(() -> new LinearGroupActivity("g1",
                "g1Start", "g1End",
                ActivityContext.ALL_SUBTASKS_JSLT,
                Arrays.asList(p1)));

        assertThrows(ConfigurationException.class, () -> new LinearGroupActivity("g2",
                "g2Start", "g2End",
                ActivityContext.ALL_SUBTASKS_JSLT,
                Arrays.asList(p1)));
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

        GroupActivity group2 = new LinearGroupActivity("g2",
                "g2Start", "g2End",
                ActivityContext.ALL_SUBTASKS_JSLT,
                Arrays.asList(p2));

        assertThrows(ConfigurationException.class, () ->
                new LinearFunction("c1", Arrays.asList(p1, group2), Object.class, Object.class));
    }
}
