package io.github.manhnt217.task.sample;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import io.github.manhnt217.task.task_engine.persistence.model.TaskDto;
import io.github.manhnt217.task.task_engine.persistence.service.TaskService;
import io.github.manhnt217.task.task_engine.task.Task;
import io.github.manhnt217.task.task_engine.task.TaskResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author manhnguyen
 */
public class JsonBasedTaskResolver implements TaskResolver {

    private final Map<String, Task> tasks;

    public JsonBasedTaskResolver(String jsonFileName) throws IOException, ConfigurationException {

        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(jsonFileName);
        List<TaskDto> taskDtos = TestUtil.OM.readValue(resourceAsStream, new TypeReference<List<TaskDto>>() {});

        tasks = new HashMap<>();

        for (TaskDto taskDto : taskDtos) {
            register(TaskService.instance().buildTask(taskDto));
        }
    }

    @Override
    public Task resolve(String name) {
        return tasks.get(name);
    }

    @Override
    public void register(Task task) {
        tasks.put(task.getName(), task);
    }
}
