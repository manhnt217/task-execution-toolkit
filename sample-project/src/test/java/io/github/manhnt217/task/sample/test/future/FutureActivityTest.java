package io.github.manhnt217.task.sample.test.future;

import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.NullNode;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.OutboundMessage;
import io.github.manhnt217.task.core.activity.SimpleInboundMessage;
import io.github.manhnt217.task.core.activity.SimpleOutboundMessage;
import io.github.manhnt217.task.core.activity.future.FutureActivity;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.task.TaskContext;
import io.github.manhnt217.task.core.type.Future;
import io.github.manhnt217.task.core.type.ObjectRef;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.sample.test.AbstractEngineTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Captor
    private ArgumentCaptor<InboundMessage> inputCaptor;

    @Test
    public void testFutureActivityShouldReturnImmediately() throws Exception {

        AtomicBoolean activityReturned = new AtomicBoolean(false);

        String fooPluginName = "foo";
        PluginActivity fooPlugin = mock(PluginActivity.class);
        given(fooPlugin.process(inputCaptor.capture(), any())).willAnswer(invocation -> {
            Thread.sleep(1000);
            //Should've returned already
            assertThat(activityReturned.get(), is(true));
            return SimpleOutboundMessage.of(new IntNode(42));
        });
        given(fooPlugin.getName()).willReturn(fooPluginName);
        given(fooPlugin.registerOutput()).willReturn(true);

        FutureActivity f1 = ActivityBuilder
                .future("f1")
                .start("f1start")
                .linkFromStart(fooPlugin)
                .linkToEnd(fooPlugin)
                .end("f1end")
                .outputMapping("." + fooPluginName)
                .build();

        TaskContext context = new TaskContext(null, repo, null, logger);
        OutboundMessage output = f1.process(SimpleInboundMessage.of(NullNode.getInstance()), context);
        activityReturned.set(true);

        assertTrue(output.getContent().isTextual());
        String refId = output.getContent().textValue();
        ObjectRef futureRef = context.resolveRef(refId);
        assertTrue(futureRef.get() instanceof Future);
        assertTrue(((Future<?>) futureRef.get()).get() instanceof IntNode);
        assertThat(((IntNode) ((Future<?>) futureRef.get()).get()).intValue(), is(42));
    }
}
