package io.github.manhnt217.task.sample.test.trial;

import com.fasterxml.jackson.databind.node.NullNode;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.activity.trial.Trial;
import io.github.manhnt217.task.core.activity.trial.TrialActivity;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.task.RootContext;
import io.github.manhnt217.task.core.task.function.Function;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.sample.test.AbstractEngineTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.github.manhnt217.task.core.context.ActivityContext.from;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author manhnguyen
 */
@ExtendWith(MockitoExtension.class)
public class TrialActivityTest extends AbstractEngineTest {

    @Test
    public void testTrial_ShouldNotThrowException() throws Exception {
        String message = "Tada";
        PluginActivity throwNPE = mockPlugin("throwNPE", invocation -> {
            throw new NullPointerException(message);
        });

        TrialActivity catchNPE = ActivityBuilder
                .trial("catchNPE", ActivityException.class, false)
                .start("s1")
                .linkFromStart(throwNPE)
                .linkToEnd(throwNPE)
                .end("e1")
                .outputMapping(from(throwNPE))
                .build();

        Function<Void, Trial> func = buildLinearFunc("f1", Void.class, Trial.class, from(catchNPE), catchNPE);
        Trial rs = func.exec(null, new RootContext(null, repo, logger));

        Throwable ex = rs.getFailure().get();
        assertThat(ex, instanceOf(ActivityException.class));
        assertThat(((ActivityException) ex).getActivityName(), is(throwNPE.getName()));
    }

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
                .outputMapping(from(throwNPE))
                .build();

        Function<Void, Trial> func = buildLinearFunc("f1", Void.class, Trial.class, from(catchNPE), catchNPE);

        Trial result = func.exec(null, new RootContext(null, repo, logger));

        assertThat(result.getSuccess(), is(NullNode.getInstance()));
        assertThat(result.getFailure().get(), instanceOf(NullPointerException.class));
        assertThat(result.getFailure().get().getMessage(), is(message));
    }

    @Test
    public void testTrial_NotCatchRootCause_ShouldThrowException() throws Exception {
        String message = "Tada";
        PluginActivity throwNPE = mockPlugin("throwNPE", invocation -> {
            throw new NullPointerException(message);
        });

        TrialActivity catchNPE = ActivityBuilder
                .trial("catchNPE", NullPointerException.class, false)
                .start("s1")
                .linkFromStart(throwNPE)
                .linkToEnd(throwNPE)
                .end("e1")
                .outputMapping(from(throwNPE))
                .build();

        Function<Void, Trial> func = buildLinearFunc("f1", Void.class, Trial.class, from(catchNPE), catchNPE);

        Assertions.assertThrows(ActivityException.class, () -> func.exec(null, new RootContext(null, repo, logger)));
    }
}
