package io.github.manhnt217.task.sample.test.loop;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.core.activity.DefaultTaskLogger;
import io.github.manhnt217.task.core.activity.ExecutionLog;
import io.github.manhnt217.task.core.activity.TaskLogger;
import io.github.manhnt217.task.core.activity.loop.ForEachActivity;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.exception.TaskException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.repo.EngineRepository;
import io.github.manhnt217.task.core.task.TaskContext;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.sample.LinearFunction;
import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.sample.plugin.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.github.manhnt217.task.sample.test.ComplexFunctionTest.mockBuiltInRepo;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;


import static org.mockito.Mockito.*;

/**
 * @author manh nguyen
 */
@ExtendWith(MockitoExtension.class)
public class LoopTest {


    /**
     * <img src="{@docRoot}/doc-files/images/testForEachSimple.png">
     *
     * @throws ConfigurationException
     */
    @Test
    public void testForEachSimple(@Mock EngineRepository repo, @Mock TaskLogger logger) throws ConfigurationException, IOException, TaskException {
        mockBuiltInRepo(repo);
        final String FOR_EACH_1 = "forEach1";

        PluginActivity p1 = ActivityBuilder
                .plugin("p1", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\",\"message\": \"Item \" + .f1Start.item}")
                .build();

        List<String> loopInput = Arrays.asList("a", "b", "c");

        ForEachActivity loop1 = ActivityBuilder
                .forEach()
                .name(FOR_EACH_1)
                .start("f1Start")
                .end("f1End")
                .linkFromStart(p1)
                .linkToEnd(p1)
                .inputMapping(TestUtil.OM.writeValueAsString(loopInput))
                .outputMapping(".f1Start.item + .f1Start.index")
                .build();

        LinearFunction<Object, Map> func = new LinearFunction<>("c1", Collections.singletonList(loop1), Object.class, Map.class);
        TaskContext context = new TaskContext(null, repo, logger);
        Map<String, Object> out = func.exec(null, context);

        for (int i = 0; i < 3; i++) {
            verify(logger).info(any(), any(), any(), eq("Item " + loopInput.get(i)));
        }

        assertThat(out.size(), is(1));
        assertThat(out, hasKey(FOR_EACH_1));

        assertTrue(out.get(FOR_EACH_1) instanceof List);
        List outArray = (List) out.get(FOR_EACH_1);
        assertThat(outArray.size(), is(3));
        for (int i = 0; i < 3; i++) {
            assertThat(outArray.get(i), is(loopInput.get(i) + i));
        }
    }
}
