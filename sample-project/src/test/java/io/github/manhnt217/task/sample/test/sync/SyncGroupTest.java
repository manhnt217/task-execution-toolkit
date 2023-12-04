package io.github.manhnt217.task.sample.test.sync;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import io.github.manhnt217.task.core.activity.Activity;
import io.github.manhnt217.task.core.activity.InboundMessage;
import io.github.manhnt217.task.core.activity.TaskLogger;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.context.ActivityContext;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.TaskException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.repo.EngineRepository;
import io.github.manhnt217.task.core.task.TaskContext;
import io.github.manhnt217.task.core.task.function.Function;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.sample.TestUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SyncGroupTest {

    @Order(1)
    @Test
    public void testSyncGroup() throws InterruptedException, ConfigurationException, InstantiationException, IllegalAccessException, ActivityException {
        ConcurrentLinkedQueue<Integer> result = setUp(true);
        assertThat(result, hasSize(2));
        assertThat(result, contains(1, 2));
    }

    @Order(2)
    @Test
    public void testAsyncGroup() throws InterruptedException, ConfigurationException, InstantiationException, IllegalAccessException, ActivityException {
        ConcurrentLinkedQueue<Integer> result = setUp(false);
        assertThat(result, hasSize(2));
        assertThat(result, contains(2, 2));
    }

    private static ConcurrentLinkedQueue<Integer> setUp(boolean synced) throws ConfigurationException, ActivityException, InterruptedException {
        PluginActivity pluginCall = mock(PluginActivity.class);

        Activity syncGroup = ActivityBuilder
                .group(synced)
                .name("syncGroup")
                .start("syncStart")
                .end("syncEnd")
                .linkFromStart(pluginCall)
                .linkToEnd(pluginCall)
                .build();

        Function testSyncGroup = ActivityBuilder
                .routine("testSyncGroup")
                .linkFromStart(syncGroup)
                .linkToEnd(syncGroup)
                .build();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        AtomicInteger inputSync = new AtomicInteger(0);
        ConcurrentLinkedQueue<Integer> result = new ConcurrentLinkedQueue<>();

        given(pluginCall.process(any(InboundMessage.class), any(ActivityContext.class)))
                .will((Answer<JsonNode>) invocation -> {
                    inputSync.incrementAndGet();
                    Thread.sleep(1000);
                    // the first call add "1", the second call add "2" to "result", in case of synchronization enabled (sequencial execution).
                    // otherwise, if this code run in parallel, both will add "2" to "result"
                    result.add(inputSync.get());
                    return NullNode.getInstance();
                });

        Runnable task = () -> {
            TaskContext syncContext = new TaskContext(UUID.randomUUID().toString(), null, mock(EngineRepository.class), mock(TaskLogger.class));
            JsonNode input = TestUtil.OM.createObjectNode();
            try {
                testSyncGroup.exec(input, syncContext);
            } catch (TaskException e) {
                throw new RuntimeException(e);
            }
        };

        executorService.submit(task);
        executorService.submit(task);
        executorService.shutdown();
        //noinspection ResultOfMethodCallIgnored
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        return result;
    }
}
