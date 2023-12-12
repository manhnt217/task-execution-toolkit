package io.github.manhnt217.task.sample.test.future;

import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.NullNode;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.SimpleInboundMessage;
import io.github.manhnt217.task.core.activity.future.FutureActivity;
import io.github.manhnt217.task.core.activity.future.WaitActivity;
import io.github.manhnt217.task.core.activity.future.exception.WaitActivityException;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.task.RootContext;
import io.github.manhnt217.task.core.task.function.Function;
import io.github.manhnt217.task.core.type.Future;
import io.github.manhnt217.task.core.type.ObjectRef;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.sample.test.AbstractEngineTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.manhnt217.task.core.context.ActivityContext.from;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.any;

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
                .outputMapping(from(fooPlugin))
                .build();

        RootContext context = new RootContext(null, repo, logger);
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
        int rs = 42;

        PluginActivity fooPlugin = mockPlugin("foo", invocation -> {
            Thread.sleep(1000);
            return rs;
        });
        FutureActivity f1 = ActivityBuilder
                .future("f1")
                .start("f1start")
                .linkFromStart(fooPlugin)
                .linkToEnd(fooPlugin)
                .end("f1end")
                .outputMapping(from(fooPlugin))
                .build();

        WaitActivity w1 = ActivityBuilder
                .wait("w1")
                .inputMapping("{\"future\": " + from(f1) + "}")
                .build();

        Function<Void, Integer> func = buildLinearFunc("func", Void.class, Integer.class, from(w1), f1, w1);
        Integer result = func.exec(null, new RootContext(null, repo, logger));

        assertThat(result, is(rs));
    }

    @Test
    public void testWaitOnWrongRef() throws Exception {
        PluginActivity mockPlugin = mockPlugin("mockRef", invocation -> new ObjectRef<>(1000));

        PluginActivity fooPlugin = mockPlugin("foo", invocation -> 42);

        FutureActivity f1 = ActivityBuilder
                .future("f1")
                .start("f1start")
                .linkFromStart(fooPlugin)
                .linkToEnd(fooPlugin)
                .end("f1end")
                .outputMapping(from(fooPlugin))
                .build();

        String w1Name = "w1";
        WaitActivity w1 = ActivityBuilder
                .wait(w1Name)
                .inputMapping("{\"future\": " + from(mockPlugin) + "}")
                .build();

        Function<Void, Integer> func = buildLinearFunc("func",
                Void.class, Integer.class, from(w1),
                mockPlugin, f1, w1);
        try {
            func.exec(null, new RootContext(null, repo, logger));
            fail("A TaskException should've thrown");
        } catch (ActivityException e) {
            assertTrue(e.getRootCause() instanceof WaitActivityException);
            assertThat(e.getRootActivityException().getActivityName(), is(w1Name));
        }
    }
}
