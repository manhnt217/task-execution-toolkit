package io.github.manhnt217.task.sample.test;

import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.core.activity.TaskLogger;
import io.github.manhnt217.task.core.repo.EngineRepository;
import io.github.manhnt217.task.core.task.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

public abstract class AbstractEngineTest {

    public static final Map<String, Class<? extends Plugin<?, ?>>> PLUGINS = ImmutableMap.of(
            "AddTwoNumber", io.github.manhnt217.task.sample.plugin.AddTwoNumber.class,
            "Curl", io.github.manhnt217.task.sample.plugin.Curl.class,
            "Log", io.github.manhnt217.task.sample.plugin.Log.class,
            "ObjectRefConsumer", io.github.manhnt217.task.sample.plugin.ObjectRefConsumer.class,
            "ObjectRefProducer", io.github.manhnt217.task.sample.plugin.ObjectRefProducer.class
    );

    @Mock
    protected EngineRepository repo;

    @Mock
    protected TaskLogger logger;

    @BeforeEach
    protected void setup () {
        lenient().when(repo.resolvePluginClass(any(String.class)))
                .then(invocation -> {
                    String pluginName = invocation.getArgument(0);
                    return PLUGINS.get(pluginName);
                });
    }
}
