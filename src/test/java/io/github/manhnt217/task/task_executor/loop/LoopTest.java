package io.github.manhnt217.task.task_executor.loop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.task_executor.LinearCompositeTask;
import io.github.manhnt217.task.task_executor.TestUtil;
import io.github.manhnt217.task.task_executor.activity.ActivityExecutionException;
import io.github.manhnt217.task.task_executor.activity.ConfigurationException;
import io.github.manhnt217.task.task_executor.activity.impl.DefaultActivityLogger;
import io.github.manhnt217.task.task_executor.activity.impl.ExecutionLog;
import io.github.manhnt217.task.task_executor.activity.impl.Group;
import io.github.manhnt217.task.task_executor.activity.impl.TaskBasedActivity;
import io.github.manhnt217.task.task_executor.activity.impl.loop.ForEachActivity;
import io.github.manhnt217.task.task_executor.task.CompositeTask;
import org.junit.jupiter.api.Test;

import java.util.*;

import static io.github.manhnt217.task.task_executor.TestUtil.OM;
import static io.github.manhnt217.task.task_executor.context.ActivityContext.ALL_SUBTASKS_JSLT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author manhnguyen
 */
public class LoopTest {


    /**
     * <img src="{@docRoot}/doc-files/images/testForEachSimple.png">
     * @throws ConfigurationException
     * @throws ActivityExecutionException
     */
    @Test
    public void testForEachSimple() throws ConfigurationException, ActivityExecutionException, JsonProcessingException {

        final String FOR_EACH_1 = "forEach1";

        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        TaskBasedActivity task1 = new TaskBasedActivity("task1");
        task1.setInputMapping("{\"severity\": \"INFO\",\"message\": \"Item \" + .f1Start.item}");
        task1.setTask(TestUtil.loadTask("LogTask"));

        ForEachActivity loop1 = new ForEachActivity(FOR_EACH_1, "f1Start", "f1End", ".f1Start.item + .f1Start.index");
        loop1.linkFromStart(task1, null);
        loop1.linkToEnd(task1);

        List<String> loopInput = Arrays.asList("a", "b", "c");
        loop1.setInputMapping(OM.writeValueAsString(loopInput)); // 3 items

        TaskBasedActivity topLevelActivity = new TaskBasedActivity(
                "topLevelActivity",
                new LinearCompositeTask("c1", Collections.singletonList(loop1)));
        JsonNode output = TestUtil.executeActivity(topLevelActivity, null, logHandler, UUID.randomUUID().toString());
        Map<String, Object> out = OM.treeToValue(output, Map.class);

        List<ExecutionLog> logs = logHandler.getLogs();

        assertThat(logs.size(), is(3));
        for (int i = 0; i < 3; i++) {
            assertThat(logs.get(i).getContent(), is("Item " + loopInput.get(i)));
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
