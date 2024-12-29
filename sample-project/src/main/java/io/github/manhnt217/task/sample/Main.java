package io.github.manhnt217.task.sample;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import io.github.manhnt217.task.core.container.TaskContainer;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.repo.EngineRepository;

import java.io.IOException;

/**
 * @author manh nguyen
 */
public class Main {

    public static void main(String[] args) throws IOException, ConfigurationException, InterruptedException {
        ObjectNode props = TestUtil.OM.valueToTree(
                ImmutableMap.of("timerInterval", 10000L)
        );
        EngineRepository repo = new JsonBasedEngineRepository("simpleHandlerRepo.json");
        TaskContainer taskContainer = new TaskContainer(props, repo);
        taskContainer.start();
    }
}
