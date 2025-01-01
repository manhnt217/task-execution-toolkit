package io.github.manhnt217.task.play_ground.controller;

import io.github.manhnt217.task.core.activity.DefaultTaskLogger;
import io.github.manhnt217.task.core.exception.ActivityException;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import io.github.manhnt217.task.core.task.RootContext;
import io.github.manhnt217.task.core.task.TaskException;
import io.github.manhnt217.task.core.task.function.Function;
import io.github.manhnt217.task.persistence.repo.SimpleEngineRepository;
import io.github.manhnt217.task.persistence.service.TaskService;
import io.github.manhnt217.task.play_ground.dto.TaskExecutionReq;
import io.github.manhnt217.task.play_ground.dto.TaskExecutionResp;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author manhnguyen
 */
@RestController
public class TaskExecutionController {

	@PostMapping("/api/task")
	public TaskExecutionResp executeTask(@RequestBody TaskExecutionReq request) throws ConfigurationException, TaskException, ActivityException {
		var repo = new SimpleEngineRepository();
		var functionDto = request.getTaskDescription();
		Function<Map<?, ?>, Map<?, ?>> function = TaskService.instance().buildFunction(functionDto);
		repo.registerFunction(function);
		RootContext rootContext = new RootContext(null, repo, new DefaultTaskLogger());
		Map<?, ?> result = function.exec(request.getInput(), rootContext);

		var resp = new TaskExecutionResp();
		resp.setOutput(result);
		return resp;
	}
}
