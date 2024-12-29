package io.github.manhnt217.task.sample.test;

import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.core.activity.Activity;
import io.github.manhnt217.task.core.activity.TaskLogger;
import io.github.manhnt217.task.core.activity.func.FunctionCallActivity;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.repo.EngineRepository;
import io.github.manhnt217.task.core.task.function.Function;
import io.github.manhnt217.task.core.task.plugin.Plugin;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.persistence.builder.FunctionBuilder;
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

    protected Function<Void, Void> buildLinearRoutine(String funcName, Activity... children) throws ConfigurationException {
        FunctionBuilder<Void, Void> builder = ActivityBuilder.routine(funcName);
        if (children.length == 0) {
            throw new IllegalArgumentException("Empty children");
        }
        builder.linkFromStart(children[0]);
        int i = 0;
        for (; i < children.length - 1; i++) {
            builder.link(children[i], children[i + 1]);
        }
        builder.linkToEnd(children[i]);
        Function<Void, Void> func = builder.build();
        lenient().when(repo.getFunction(funcName)).thenReturn(func);
        return func;
    }

    protected <P, R> FunctionCallActivity buildSingleActivityFunctionCall(String activityName, Activity childActivity, Class<? extends P> inputType, Class<? extends R> outputType, String inputMapping, String outputMapping) throws ConfigurationException {
        String funcName = activityName + "func";
        Function<? extends P, ? extends R> func = ActivityBuilder
                .function(funcName, inputType, outputType)
                .linkFromStart(childActivity)
                .linkToEnd(childActivity)
                .outputMapping(outputMapping)
                .build();
        lenient().when(repo.getFunction(funcName)).thenReturn(func);
        return ActivityBuilder
                .funcCall(activityName)
                .inputMapping(inputMapping)
                .funcName(funcName)
                .build();
    }

    protected PluginActivity buildPluginActivity(String actName, String pluginName, String inputMapping) {
        return ActivityBuilder
                .plugin(actName, pluginName)
                .inputMapping(inputMapping)
                .build();
    }
}
