package io.github.manhnt217.task.sample.test.trial;

import com.fasterxml.jackson.databind.node.NullNode;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.activity.trial.Trial;
import io.github.manhnt217.task.core.activity.trial.TrialActivity;
import io.github.manhnt217.task.core.task.RootContext;
import io.github.manhnt217.task.core.task.function.Function;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.sample.test.AbstractEngineTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author manhnguyen
 */
@ExtendWith(MockitoExtension.class)
public class TrialActivityTest extends AbstractEngineTest {

    @Test
    public void testTrial_CatchRootCause_ShouldNotThrowException() throws Exception {
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
                .outputMapping(".throwNPE")
                .build();

        Function<Void, Trial> func = buildLinearFunc("f1", Void.class, Trial.class, ".catchNPE", catchNPE);

        Trial result = func.exec(null, new RootContext(null, repo, logger));

        assertThat(result.getSuccess(), is(NullNode.getInstance()));
        assertThat(result.getFailure().get(), instanceOf(NullPointerException.class));
        assertThat(result.getFailure().get().getMessage(), is(message));
    }
}
