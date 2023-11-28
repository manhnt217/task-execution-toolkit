package io.github.manhnt217.task.sample.test.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.core.activity.DefaultTaskLogger;
import io.github.manhnt217.task.core.exception.TaskException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.sample.JsonBasedEngineRepository;
import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.sample.Util;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author manh nguyen
 */
public class PersistenceTest {

    @Test
    public void testLoadAndExecuteCompositeTask() throws IOException, ConfigurationException, TaskException {
        JsonBasedEngineRepository taskRepo = new JsonBasedEngineRepository("complexTaskRepo.json");

        JsonNode input1 = Util.OM.valueToTree(ImmutableMap.of(
                "a", 22,
                "b", 20
        ));

        DefaultTaskLogger logHandler1 = new DefaultTaskLogger();
        Integer result1 = Util.OM.treeToValue(
                TestUtil.executeFunc("c1", null, input1, logHandler1, UUID.randomUUID().toString(), taskRepo), Integer.class);
        assertThat(result1, is(42));
        assertThat(logHandler1.getLogs(), hasSize(1));
        assertThat(logHandler1.getLogs().get(0).getContent(), is("The result of Add task is: 42"));

        JsonNode input2 = Util.OM.valueToTree(ImmutableMap.of(
                "a", 22222,
                "b", 20
        ));

        DefaultTaskLogger logHandler2 = new DefaultTaskLogger();
        Integer result2 = Util.OM.treeToValue(
                TestUtil.executeFunc("c1", null, input2, logHandler2, UUID.randomUUID().toString(), taskRepo), Integer.class);
        assertThat(result2, is(22242));
        assertThat(logHandler2.getLogs(), hasSize(0));

    }
}
