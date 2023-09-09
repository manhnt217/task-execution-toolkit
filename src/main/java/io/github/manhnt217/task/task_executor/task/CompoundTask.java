package io.github.manhnt217.task.task_executor.task;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import io.github.manhnt217.task.task_executor.process.ExecutionLog;
import io.github.manhnt217.task.task_executor.process.Severity;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.manhnt217.task.task_executor.Main.om;

@Getter
@Setter
public class CompoundTask extends Task {

	private Set<Task> subTasks;

	private TaskExecutionContext context;

	public CompoundTask(Set<Task> subTasks) {
		this.subTasks = subTasks;
	}

	@Override
	public JsonNode process(JsonNode inputJSON) {

		TaskExecutionContext context = new TaskExecutionContext();

		context.saveInitialInput(inputJSON);

		List<Task> executionOrder = resolveDependencies(subTasks);
		for (Task task : executionOrder) {
			executeTask(task, context);
		}

		return context.applyTransform(outputMappingExpression);
	}

	private List<Task> resolveDependencies(Set<Task> subTasks) {
		// TODO: dummy implementation
		return subTasks.stream().collect(Collectors.toList());
	}

	private void executeTask(Task task, TaskExecutionContext context) {

		JsonNode inputAfterTransform = extractInput(task, context);

		context.saveInput(task, inputAfterTransform);
		log(task.getStartLogExpression(), context);

		JsonNode output = task.process(inputAfterTransform);
		this.logs.addAll(task.getLogs());

		context.saveOutput(task, output);
		log(task.getEndLogExpression(), context);
	}

	private static JsonNode extractInput(Task task, TaskExecutionContext context) {
		if (task.inputType == null) {
			if (task.isIndependent()) {
				return context.applyTransform(task.getInputMappingExpression());
			} else if (task.isMonoDependent()) {
				return context.applyFromTaskOutput(task.getDependencies().get(0), task.getInputMappingExpression());
			} else {
				throw new TaskExecutionException("Task has more than one dependencies MUST HAVE inputType = CONTEXT");
			}
		} else if (InputType.CONTEXT.equals(task.inputType)) {
			return context.applyTransform(task.getInputMappingExpression());
		} else if (InputType.PREVIOUS_TASK.equals(task.inputType) && task.getDependencies().size() == 1) {
			return context.applyFromTaskOutput(task.getDependencies().get(0), task.getInputMappingExpression());
		} else {
			throw new TaskExecutionException("A task should depend on ONLY ONE task when its InputType = Previous Task");
		}
	}

	private void log(String jslt, TaskExecutionContext ctx) {
		if (StringUtils.isBlank(jslt)) {
			return;
		}
		try {
			JsonNode jsonNode = ctx.applyTransform(jslt);
			logs.add(new ExecutionLog(Severity.INFO, jsonNode.isContainerNode() ? om.writeValueAsString(jsonNode) : jsonNode.asText()));
		} catch (Exception e) {
			logs.add(new ExecutionLog(Severity.WARN, "Error while applying log expression to the context. Expression = " + jslt));
		}
	}
}
