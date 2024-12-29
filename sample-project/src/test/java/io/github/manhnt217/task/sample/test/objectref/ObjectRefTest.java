package io.github.manhnt217.task.sample.test.objectref;

import io.github.manhnt217.task.sample.LinearCompositeTask;
import io.github.manhnt217.task.sample.TestUtil;
import io.github.manhnt217.task.task_engine.activity.DefaultActivityLogger;
import io.github.manhnt217.task.task_engine.activity.task.TaskBasedActivity;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * @author manhnguyen
 */
@Execution(ExecutionMode.CONCURRENT)
class ObjectRefTest {

    @Test
    public void testSimpleObjectRef() throws ConfigurationException {
        DefaultActivityLogger logHandler = new DefaultActivityLogger();

        TaskBasedActivity objectRefProducerTask1 = new TaskBasedActivity("objectRefProducerTask1");
        objectRefProducerTask1.setTask(TestUtil.loadTask("ObjectRefProducerTask"));

        TaskBasedActivity objectRefConsumerTask1 = new TaskBasedActivity("objectRefConsumerTask1");
        objectRefConsumerTask1.setInputMapping(".objectRefProducerTask1");
        objectRefConsumerTask1.setTask(TestUtil.loadTask("ObjectRefConsumerTask"));

        LinearCompositeTask task = new LinearCompositeTask("c1", Arrays.asList(objectRefProducerTask1, objectRefConsumerTask1));

        assertDoesNotThrow(() ->
                TestUtil.executeTask(task, null, null, logHandler, UUID.randomUUID().toString()));
    }
}
