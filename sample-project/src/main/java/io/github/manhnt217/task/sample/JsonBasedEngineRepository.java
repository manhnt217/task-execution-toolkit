package io.github.manhnt217.task.sample;

import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.task.event.EventSourceConfig;
import io.github.manhnt217.task.persistence.model.HandlerDto;
import io.github.manhnt217.task.persistence.model.PluginDto;
import io.github.manhnt217.task.persistence.model.RepoDto;
import io.github.manhnt217.task.persistence.model.FunctionDto;
import io.github.manhnt217.task.persistence.service.TaskService;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author manh nguyen
 */
public class JsonBasedEngineRepository extends SimpleEngineRepository {

    public JsonBasedEngineRepository(String jsonFileName) throws IOException, ConfigurationException {
        super();

        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(jsonFileName);
        RepoDto repoDto = TestUtil.OM.readValue(resourceAsStream, RepoDto.class);

        if (repoDto.getFunctions() != null) {
            for (FunctionDto functionDto : repoDto.getFunctions()) {
                registerFunction(TaskService.instance().buildFunction(functionDto));
            }
        }

        if (repoDto.getPlugins() != null) {
            for (PluginDto plugin : repoDto.getPlugins()) {
                registerFunctionPlugin(plugin);
            }
        }

        if (repoDto.getHandlers() != null) {
            for (HandlerDto handlerDto : repoDto.getHandlers()) {
                registerHandler(TaskService.instance().buildHandler(handlerDto));
            }
        }

        if (repoDto.getSources() != null) {
            for (EventSourceConfig eventSourceConfig : repoDto.getSources()) {
                registerEventSource(eventSourceConfig);
            }
        }
    }
}
