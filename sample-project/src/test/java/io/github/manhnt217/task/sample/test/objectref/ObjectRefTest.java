package io.github.manhnt217.task.sample.test.objectref;

import io.github.manhnt217.task.core.activity.DefaultTaskLogger;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.sample.LinearFunction;
import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.sample.plugin.ObjectRefConsumer;
import io.github.manhnt217.task.sample.plugin.ObjectRefProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author manh nguyen
 */
@Execution(ExecutionMode.CONCURRENT)
class ObjectRefTest {

    @Test
    public void testSimpleObjectRef() throws ConfigurationException {
        DefaultTaskLogger logHandler = new DefaultTaskLogger();

        PluginActivity act1 = ActivityBuilder
                .plugin("act1", ObjectRefProducer.class.getSimpleName())
                .build();

        PluginActivity act2 = ActivityBuilder
                .plugin("act2", ObjectRefConsumer.class.getSimpleName())
                .inputMapping(".act1")
                .build();

        LinearFunction func = new LinearFunction("c1", Arrays.asList(act1, act2));

        assertDoesNotThrow(() ->
                TestUtil.executeFunc(func, null, null, logHandler, UUID.randomUUID().toString()));
    }
}
