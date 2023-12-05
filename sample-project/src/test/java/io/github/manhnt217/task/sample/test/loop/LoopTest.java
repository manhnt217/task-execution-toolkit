package io.github.manhnt217.task.sample.test.loop;

import io.github.manhnt217.task.core.activity.loop.ForEachActivity;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.exception.TaskException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.task.TaskContext;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.sample.LinearFunction;
import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.sample.plugin.Log;
import io.github.manhnt217.task.sample.test.AbstractEngineTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author manh nguyen
 */
@ExtendWith(MockitoExtension.class)
public class LoopTest extends AbstractEngineTest {


    /**
     * <img src="{@docRoot}/doc-files/images/testForEachSimple.png">
     *
     * @throws ConfigurationException
     */
    @Test
    public void testForEachSimple() throws ConfigurationException, IOException, TaskException {
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
