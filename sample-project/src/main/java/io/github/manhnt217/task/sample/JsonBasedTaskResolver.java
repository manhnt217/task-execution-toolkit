package io.github.manhnt217.task.sample;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import io.github.manhnt217.task.task_engine.persistence.model.TaskDto;
import io.github.manhnt217.task.task_engine.persistence.service.TaskService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author manhnguyen
 */
public class JsonBasedTaskResolver extends SimpleTaskResolver {

    public JsonBasedTaskResolver(String jsonFileName) throws IOException, ConfigurationException {
        super();

        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(jsonFileName);
        List<TaskDto> taskDtos = TestUtil.OM.readValue(resourceAsStream, new TypeReference<List<TaskDto>>() {});

        for (TaskDto taskDto : taskDtos) {
            register(TaskService.instance().buildTask(taskDto));
        }
    }

}
