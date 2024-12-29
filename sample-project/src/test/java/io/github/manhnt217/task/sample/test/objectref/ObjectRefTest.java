package io.github.manhnt217.task.sample.test.objectref;

import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.task.TaskContext;
import io.github.manhnt217.task.core.task.function.Function;
import io.github.manhnt217.task.sample.test.AbstractEngineTest;
import io.github.manhnt217.task.sample.test.example_plugin.ObjectRefConsumer;
import io.github.manhnt217.task.sample.test.example_plugin.ObjectRefProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author manh nguyen
 */
@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
class ObjectRefTest extends AbstractEngineTest {

    @Test
    public void testSimpleObjectRef() throws ConfigurationException {

        PluginActivity act1 = buildPluginActivity("act1", ObjectRefProducer.class.getSimpleName(), null);

        PluginActivity act2 = buildPluginActivity("act2", ObjectRefConsumer.class.getSimpleName(), ".act1");

        Function<Void, Void> func = buildLinearRoutine("c1", act1, act2);

        assertDoesNotThrow(() -> {
            func.exec(null, new TaskContext(null, repo, futureProcessor, logger));
        });
    }
}
