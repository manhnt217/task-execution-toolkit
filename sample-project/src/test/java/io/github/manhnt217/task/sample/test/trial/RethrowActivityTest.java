package io.github.manhnt217.task.sample.test.trial;

import com.fasterxml.jackson.databind.node.NullNode;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.SimpleInboundMessage;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.activity.trial.*;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.context.JSONUtil;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.task.RootContext;
import io.github.manhnt217.task.core.task.TaskException;
import io.github.manhnt217.task.core.task.function.Function;
import io.github.manhnt217.task.core.type.ObjectRef;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.sample.test.AbstractEngineTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.github.manhnt217.task.core.context.ActivityContext.from;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
public class RethrowActivityTest extends AbstractEngineTest {

    @Test
    public void testThrowActivity_ShouldThrowException() {
        String msg = "An exception was thrown";
        String rootCauseMsg = "This is root cause";
        RethrowActivity rethrowAct = new RethrowActivity("rethrowAct");
        ActivityContext context = new RootContext(null, repo, logger);
        Rethrow input = new Rethrow() {{
            setMessage(msg);
            setEx(new ObjectRef<>(new RuntimeException(rootCauseMsg)));
        }};
        InboundMessage in = SimpleInboundMessage.of(JSONUtil.valueToTree(input, context));
        try {
            rethrowAct.process(in, context);
            fail("Should've thrown exception already");
        } catch (Exception e) {
            assertThat(e, instanceOf(CustomActivityException.class));
            assertThat(((CustomActivityException) e).getRootCause(), instanceOf(RuntimeException.class));
            assertThat(((CustomActivityException) e).getRootCause().getMessage(), is(rootCauseMsg));
        }
    }

    @Test
    public void testTrialAndRethrow() throws Exception {
        String message = "Tada";
        PluginActivity throwNPE = mockPlugin("throwNPE", invocation -> {
            throw new NullPointerException(message);
        });

        TrialActivity catchNPE = ActivityBuilder
                .trial("catchNPE", NullPointerException.class, true)
                .start("s1")
                .linkFromStart(throwNPE)
                .linkToEnd(throwNPE)
                .end("e1")
                .outputMapping(from(throwNPE))
                .build();

        RethrowActivity rethrow = new RethrowActivity("rethrow");
        rethrow.setInputMapping("{\"ex\":" + from(catchNPE) + ".failure" + "}");

        Function<Void, Void> func = buildLinearRoutine("f1", catchNPE, rethrow);

        try {
            func.exec(null, new RootContext(null, repo, logger));
            fail("ActivityException should've been thrown");
        } catch (TaskException e) {
            fail("Task exception should've not been thrown");
        } catch (ActivityException e) {
            assertThat(e, instanceOf(CustomActivityException.class));
            assertThat(e.getRootCause(), instanceOf(NullPointerException.class));
            assertThat(e.getRootCause().getMessage(), is(message));
        }
    }
}
