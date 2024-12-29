package io.github.manhnt217.task.sample.test.future;

import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.NullNode;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.SimpleInboundMessage;
import io.github.manhnt217.task.core.activity.future.FutureActivity;
import io.github.manhnt217.task.core.activity.future.WaitActivity;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.exception.TaskException;
import io.github.manhnt217.task.core.task.TaskContext;
import io.github.manhnt217.task.core.task.function.Function;
import io.github.manhnt217.task.core.task.plugin.Plugin;
import io.github.manhnt217.task.core.type.Future;
import io.github.manhnt217.task.core.type.ObjectRef;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.sample.test.AbstractEngineTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.*;

/**
 * @author manhnguyen
 */
@ExtendWith(MockitoExtension.class)
public class FutureActivityTest extends AbstractEngineTest {

    @Test
    public void testFutureActivityShouldReturnImmediately() throws Exception {

        AtomicBoolean activityReturned = new AtomicBoolean(false);

        String fooPluginName = "foo";

        PluginActivity fooPlugin = mockPlugin(fooPluginName, invocation -> {
            Thread.sleep(1000);
            assertThat("Should've returned already", activityReturned.get(), is(true));
            return 42;
        });

        FutureActivity f1 = ActivityBuilder
                .future("f1")
                .start("f1start")
                .linkFromStart(fooPlugin)
                .linkToEnd(fooPlugin)
                .end("f1end")
                .outputMapping("." + fooPluginName)
                .build();

        TaskContext context = new TaskContext(null, repo, logger);
        OutboundMessage output = f1.process(SimpleInboundMessage.of(NullNode.getInstance()), context);
        activityReturned.set(true);

        assertTrue(output.getContent().isTextual());
        String refId = output.getContent().textValue();
        ObjectRef futureRef = context.resolveRef(refId);
        assertTrue(futureRef.get() instanceof Future);
        assertTrue(((Future<?>) futureRef.get()).get() instanceof IntNode);
        assertThat(((IntNode) ((Future<?>) futureRef.get()).get()).intValue(), is(42));
    }

    @Test
    public void testFutureAndWait() throws Exception {
        String fooPluginName = "foo";
        int rs = 42;

        PluginActivity fooPlugin = mockPlugin(fooPluginName, invocation -> {
            Thread.sleep(1000);
            return rs;
        });
        String f1Name = "f1";
        FutureActivity f1 = ActivityBuilder
                .future(f1Name)
                .start("f1start")
                .linkFromStart(fooPlugin)
                .linkToEnd(fooPlugin)
                .end("f1end")
                .outputMapping("." + fooPluginName)
                .build();

        String w1Name = "w1";
        WaitActivity w1 = ActivityBuilder
                .wait(w1Name)
                .inputMapping("{\"future\": .f1}")
                .build();

        Function<Void, Integer> func = buildLinearFunc("func", Void.class, Integer.class, "." + w1Name, f1, w1);
        Integer result = func.exec(null, new TaskContext(null, repo, logger));

        assertThat(result, is(rs));
    }

    @Test
    public void testWaitOnWrongRef() throws Exception {
        String mockPluginName = "mockRef";
        PluginActivity mockPlugin = mockPlugin(mockPluginName, invocation -> new ObjectRef<>(1000));

        String fooPluginName = "foo";
        PluginActivity fooPlugin = mockPlugin(fooPluginName, invocation -> 42);

        String f1Name = "f1";
        FutureActivity f1 = ActivityBuilder
                .future(f1Name)
                .start("f1start")
                .linkFromStart(fooPlugin)
                .linkToEnd(fooPlugin)
                .end("f1end")
                .outputMapping("." + fooPluginName)
                .build();

        String w1Name = "w1";
        WaitActivity w1 = ActivityBuilder
                .wait(w1Name)
                .inputMapping("{\"future\": ." + mockPluginName + "}")
                .build();

        Function<Void, Integer> func = buildLinearFunc("func",
                Void.class, Integer.class, "." + w1Name,
                mockPlugin, f1, w1);
        try {
            func.exec(null, new TaskContext(null, repo, logger));
            fail("A TaskException should've thrown");
        } catch (TaskException e) {
            assertTrue(e.getRootCause() instanceof ClassCastException);
            assertTrue(e.getRootActivityException().getActivity() instanceof WaitActivity);
        }
    }

    private PluginActivity mockPlugin(String activityName, Answer<?> answer) throws Exception {
        Plugin mockPlugin = mock(Plugin.class, Answers.CALLS_REAL_METHODS);
        given(mockPlugin.getInputType()).willReturn(Object.class);
        given(mockPlugin.exec(any(), any())).willAnswer(answer);
        String randomPluginName = UUID.randomUUID().toString();
        given(repo.resolvePlugin(randomPluginName)).willReturn(mockPlugin);
        PluginActivity mockPluginAct = ActivityBuilder.plugin(activityName, randomPluginName).build();
        return mockPluginAct;
    }
}
