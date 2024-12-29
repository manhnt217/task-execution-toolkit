package io.github.manhnt217.task.sample.test.objectref;

import io.github.manhnt217.task.core.activity.TaskLogger;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.repo.EngineRepository;
import io.github.manhnt217.task.core.task.TaskContext;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.sample.LinearFunction;
import io.github.manhnt217.task.sample.plugin.ObjectRefConsumer;
import io.github.manhnt217.task.sample.plugin.ObjectRefProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static io.github.manhnt217.task.sample.test.ComplexFunctionTest.mockBuiltInRepo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * @author manh nguyen
 */
@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
class ObjectRefTest {

    @Test
    public void testSimpleObjectRef(@Mock EngineRepository repo, @Mock TaskLogger logger) throws ConfigurationException {
        mockBuiltInRepo(repo);

        PluginActivity act1 = ActivityBuilder
                .plugin("act1", ObjectRefProducer.class.getSimpleName())
                .build();

        PluginActivity act2 = ActivityBuilder
                .plugin("act2", ObjectRefConsumer.class.getSimpleName())
                .inputMapping(".act1")
                .build();

        LinearFunction<Void, Void> func = new LinearFunction<>("c1", Arrays.asList(act1, act2), Void.class, Void.class);

        assertDoesNotThrow(() -> {
            TaskContext context = new TaskContext(null, repo, logger);
            func.exec(null, context);
        });
    }
}
