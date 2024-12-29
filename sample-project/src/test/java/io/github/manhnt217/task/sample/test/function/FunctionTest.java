package io.github.manhnt217.task.sample.test.function;

import io.github.manhnt217.task.core.activity.func.FunctionCallActivity;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.task.RootContext;
import io.github.manhnt217.task.core.task.TaskException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.task.function.Function;
import io.github.manhnt217.task.core.type.ObjectRef;
import io.github.manhnt217.task.sample.test.AbstractEngineTest;
import io.github.manhnt217.task.sample.example_plugin.ObjectRefConsumer;
import io.github.manhnt217.task.sample.example_plugin.ObjectRefProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.github.manhnt217.task.core.task.function.Function.*;

@ExtendWith(MockitoExtension.class)
public class FunctionTest extends AbstractEngineTest {

    @Test
    public void testPassingObjectRefBetweenFunction() throws ConfigurationException, TaskException {
        PluginActivity refproducer = buildPluginActivity("refproducer", ObjectRefProducer.class.getSimpleName(), null);
        PluginActivity refconsumer = buildPluginActivity("refconsumer", ObjectRefConsumer.class.getSimpleName(), START_INPUT_MAPPING);
        FunctionCallActivity callp1 = buildSingleActivityFunctionCall("callp1", refproducer, Void.class, ObjectRef.class, null, ".refproducer");
        FunctionCallActivity callp2 = buildSingleActivityFunctionCall("callp2", refconsumer, ObjectRef.class, Void.class, ".callp1", null);

        Function<Void, Void> func = buildLinearRoutine("func", callp1, callp2);

        Assertions.assertDoesNotThrow(() -> func.exec(null, new RootContext(null, repo, logger)));
    }
}
