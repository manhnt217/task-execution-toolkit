package io.github.manhnt217.task.sample.test;

import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.core.activity.Activity;
import io.github.manhnt217.task.core.activity.TaskLogger;
import io.github.manhnt217.task.core.activity.func.FunctionCallActivity;
import io.github.manhnt217.task.core.activity.group.GroupActivity;
import io.github.manhnt217.task.core.activity.plugin.PluginActivity;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.repo.EngineRepository;
import io.github.manhnt217.task.core.task.function.Function;
import io.github.manhnt217.task.core.task.plugin.Plugin;
import io.github.manhnt217.task.persistence.builder.ActivityBuilder;
import io.github.manhnt217.task.persistence.builder.FunctionBuilder;
import io.github.manhnt217.task.persistence.builder.GroupActivityBuilder;
import io.github.manhnt217.task.sample.example_plugin.AddTwoNumber;
import io.github.manhnt217.task.sample.example_plugin.Curl;
import io.github.manhnt217.task.sample.example_plugin.ObjectRefConsumer;
import io.github.manhnt217.task.sample.example_plugin.ObjectRefProducer;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

public abstract class AbstractEngineTest {

    public static final Map<String, Class<? extends Plugin<?, ?>>> PLUGINS = ImmutableMap.of(
            "AddTwoNumber", AddTwoNumber.class,
            "Curl", Curl.class,
            "Log", io.github.manhnt217.task.sample.plugin.Log.class,
            "ObjectRefConsumer", ObjectRefConsumer.class,
            "ObjectRefProducer", ObjectRefProducer.class
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
        return buildLinearFunc(funcName, Void.class, Void.class, null, children);
    }

    protected <P, R> Function<P, R> buildLinearFunc(String funcName,
                                                    Class<? extends P> inputType,
                                                    Class<? extends R> outputType,
                                                    String outputMapping,
                                                    Activity... children) throws ConfigurationException {
        FunctionBuilder<P, R> builder = ActivityBuilder.function(funcName, inputType, outputType);
        if (children.length == 0) {
            throw new IllegalArgumentException("Empty children");
        }
        builder.linkFromStart(children[0]);
        int i = 0;
        for (; i < children.length - 1; i++) {
            builder.link(children[i], children[i + 1]);
        }
        builder.linkToEnd(children[i]);
        builder.outputMapping(outputMapping);

        Function<P, R> func = builder.build();
        lenient().when(repo.getFunction(funcName)).thenReturn(func);
        return func;
    }

    protected GroupActivity buildLinearGroup(String actName,
                                             boolean sync,
                                             String inputMapping,
                                             String outputMapping,
                                             String startName,
                                             String endName,
                                             Activity... children) throws ConfigurationException {
        GroupActivityBuilder builder = ActivityBuilder
                .group(actName, sync)
                .start(startName)
                .end(endName)
                .inputMapping(inputMapping);
        if (children.length == 0) {
            throw new IllegalArgumentException("Empty children");
        }
        builder.linkFromStart(children[0]);
        int i = 0;
        for (; i < children.length - 1; i++) {
            builder.link(children[i], children[i + 1]);
        }
        builder.linkToEnd(children[i]);
        builder.outputMapping(outputMapping);

        return builder.build();
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
