package io.github.manhnt217.task.sample.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.core.activity.TaskLogger;
import io.github.manhnt217.task.core.activity.func.FunctionCallActivity;
import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.exception.TaskException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.repo.EngineRepository;
import io.github.manhnt217.task.core.task.TaskContext;
import io.github.manhnt217.task.core.task.function.Function;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.sample.example_plugin.Curl;
import io.github.manhnt217.task.sample.plugin.Log;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Map;

import static io.github.manhnt217.task.core.context.ActivityContext.ALL_SUBTASKS_JSLT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * @author manh nguyen
 */
@SuppressWarnings("ALL")
@ExtendWith(MockitoExtension.class)
public class ComplexFunctionTest extends AbstractEngineTest {

    @Test
    public void testComplex1() throws IOException, ConfigurationException, TaskException {
        PluginActivity p1 = ActivityBuilder
                .plugin("p1", Curl.class.getSimpleName())
                .inputMapping(ActivityContext.FROM_PROPS)
                .build();

        PluginActivity p2 = ActivityBuilder
                .plugin("p2", Log.class.getSimpleName())
                .inputMapping("{\"severity\": \"INFO\", \"message\": \"Status code is \" + .p1.statusCode}")
                .build();

        Function<Object, Map> func = buildLinearFunc("t1", Object.class, Map.class, ALL_SUBTASKS_JSLT, p1, p2);

        Map<String, Object> props = ImmutableMap.of(
                "url", "https://example.com",
                "method", "GET"
        );
        TaskContext taskContext = new TaskContext("uuid", TestUtil.OM.valueToTree(props), repo, logger);
        Map<String, ?> out = func.exec(null, taskContext);
        assertThat(out.size(), is(2));
        assertThat(out, hasKey("p1"));
        assertThat(out, hasKey("p2"));

        assertThat((Map<String, Object>) out.get("p1"), hasKey("statusCode"));
        assertThat(((Map) out.get("p2")).size(), is(0));

        verify(logger, atLeastOnce()).info(any(), any(), any(), any());
        verify(logger).info(any(), any(), any(), eq("Status code is 200"));
    }

    @Test
    public void testComplex2_PassingInputFromParent() throws IOException, ConfigurationException, TaskException {

        PluginActivity act1 = ActivityBuilder
                .plugin("act1", Curl.class.getSimpleName())
                .inputMapping("{\"url\": ." + Function.START_ACTIVITY_NAME + ".url, \"method\": \"GET\"}")
                .build();

        Function<Map, Map> func = buildLinearFunc("c1", Map.class, Map.class, ALL_SUBTASKS_JSLT,  act1);

        Map<String, ?> out = func.exec(
                ImmutableMap.of("url", "https://example.com"),
                new TaskContext("uuid", null, repo, logger));
        assertThat(out.size(), is(2));
        assertThat(out, hasKey(Function.START_ACTIVITY_NAME));
        assertThat(out, hasKey("act1"));

        assertThat((Map<String, Object>) out.get("act1"), hasKey("statusCode"));
    }

    @Test
    public void testRecursive(@Mock EngineRepository repo, @Mock TaskLogger logger) throws ConfigurationException, TaskException, JsonProcessingException {

        // Calculate the factorial
        String taskName = "r1";
        FunctionCallActivity callr1Activity = ActivityBuilder
                .funcCall("callr1")
                .funcName(taskName) // recursive call
                .inputMapping(".START | {\"n\": .n - 1, \"acc\": .acc * .n}")
                .build();
        Function<RecursiveInput, Integer> r1 = ActivityBuilder
                .function(taskName, RecursiveInput.class, Integer.class)
                .linkStartToEnd(".START.n == 1")
                .linkFromStart(callr1Activity, Group.OTHERWISE_GUARD_EXP)
                .linkToEnd(callr1Activity)
                .outputMapping("if (.callr1) .callr1 else .START.acc")
                .build();

        int n = 7;
        given(repo.getFunction(taskName)).willReturn(r1);

        TaskContext context = new TaskContext(r1.getName(), null, repo, logger);
        Integer result = r1.exec(new RecursiveInput(n, 1), context);
        assertThat(result, is(factorial(n)));
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class RecursiveInput {
        private int n;
        private int acc;
    }

    private int factorial(int n) {
        int f = 1;
        for (int i = 1; i <= n; i++) {
            f = f * i;
        }
        return f;
    }
}
