package io.github.manhnt217.task.sample.test.objectref;

import io.github.manhnt217.task.sample.LinearCompositeTask;
import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.sample.plugin.ObjectRefConsumerTask;
import io.github.manhnt217.task.sample.plugin.ObjectRefProducerTask;
import io.github.manhnt217.task.task_engine.activity.DefaultActivityLogger;
import io.github.manhnt217.task.task_engine.activity.task.TaskBasedActivity;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import io.github.manhnt217.task.task_engine.persistence.builder.ActivityBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author manhnguyen
 */
@Execution(ExecutionMode.CONCURRENT)
class ObjectRefTest {

    @Test
    public void testSimpleObjectRef() throws ConfigurationException {
        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        TaskBasedActivity objectRefProducerTask1 = ActivityBuilder
                .task("objectRefProducerTask1")
                .taskName(ObjectRefProducerTask.class.getName())
                .build();

        TaskBasedActivity objectRefConsumerTask1 = ActivityBuilder
                .task("objectRefConsumerTask1")
                .taskName(ObjectRefConsumerTask.class.getName())
                .inputMapping(".objectRefProducerTask1")
                .build();

        LinearCompositeTask task = new LinearCompositeTask("c1", Arrays.asList(objectRefProducerTask1, objectRefConsumerTask1));

        assertDoesNotThrow(() ->
                TestUtil.executeTask(task, null, null, logHandler, UUID.randomUUID().toString()));
    }
}
