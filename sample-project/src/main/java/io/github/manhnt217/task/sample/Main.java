package io.github.manhnt217.task.sample;

import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.core.activity.DefaultTaskLogger;
import io.github.manhnt217.task.core.container.TaskContainer;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.repo.EngineRepository;
import io.github.manhnt217.task.core.task.RootContext;
import io.github.manhnt217.task.core.task.TaskException;
import io.github.manhnt217.task.core.task.function.Function;
import io.github.manhnt217.task.sample.test.helper.SampleInput;
import io.github.manhnt217.task.sample.test.helper.SampleOutput;
import lombok.Data;

import java.io.IOException;
import java.util.Map;

/**
 * @author manh nguyen
 */
public class Main {

    public static void main(String[] args) throws Exception {
//        Foo foo = TestUtil.OM.treeToValue(NullNode.getInstance(), Foo.class);
//        System.out.println("Foo: " + (foo == null));
        testFunction();
//        testHandler();
    }

    private static void testFunction() throws Exception {
        EngineRepository repo = new JsonBasedEngineRepository("simpleFunctionRepo.json");
        RootContext rootContext = new RootContext(null, repo, new DefaultTaskLogger());

        @SuppressWarnings("unchecked")
        Function<Map<?, ?>, Map<?, ?>> simpleFunction = repo.getFunction("simpleFunction");
        Map<?, ?> rs1 = simpleFunction.exec(ImmutableMap.of(
                "name", "Lanne",
                "age", 21,
                "address", "Kyoto"
        ), rootContext);
        Map<?, ?> rs2 = simpleFunction.exec(ImmutableMap.of(
                "name", "Phil",
                "age", 7,
                "address", "Paris"
        ), rootContext);

        System.out.println("Result 1: " + rs1);
        System.out.println("Result 2: " + rs2);
    }

    private static void testHandler() throws IOException, ConfigurationException {
        ObjectNode props = TestUtil.OM.valueToTree(
                ImmutableMap.of("timerInterval", 2343L)
        );
        EngineRepository repo = new JsonBasedEngineRepository("simpleHandlerRepo.json");
        TaskContainer taskContainer = new TaskContainer(props, repo);
        taskContainer.start();
    }

    @Data
    public static class Foo {
        private String bar;
    }
}
