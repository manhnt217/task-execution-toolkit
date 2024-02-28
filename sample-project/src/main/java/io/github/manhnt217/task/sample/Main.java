package io.github.manhnt217.task.sample;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.core.container.TaskContainer;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.repo.EngineRepository;
import lombok.Data;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author manh nguyen
 */
public class Main {

    public static void main(String[] args) throws IOException, ConfigurationException, InterruptedException {
//        Foo foo = TestUtil.OM.treeToValue(NullNode.getInstance(), Foo.class);
//        System.out.println("Foo: " + (foo == null));
//        testHandler();
        testHttpServer();
    }

    private static void testHttpServer() throws ConfigurationException, IOException {
        EngineRepository repo = new JsonBasedEngineRepository("simpleHandlerRepo.json");
        TaskContainer taskContainer = new TaskContainer(null, repo);
        taskContainer.start();

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        taskContainer.shutdown();
    }

    private static void testHandler() throws IOException, ConfigurationException {
        ObjectNode props = TestUtil.OM.valueToTree(
                ImmutableMap.of("timerInterval", 2343L)
        );
        EngineRepository repo = new JsonBasedEngineRepository("simpleHandlerRepo.json");
        TaskContainer taskContainer = new TaskContainer(props, repo);
        taskContainer.start();

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        taskContainer.shutdown();
    }

    @Data
    public static class Foo {
        private String bar;
    }
}
