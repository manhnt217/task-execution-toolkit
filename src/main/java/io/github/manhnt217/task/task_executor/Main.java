package io.github.manhnt217.task.task_executor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import io.github.manhnt217.task.task_executor.task.CompoundTask;
import io.github.manhnt217.task.task_executor.task.TaskExecutionContext;
import io.github.manhnt217.task.task_executor.task.TemplateTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Map;

public class Main {

	public static final ObjectMapper om = new ObjectMapper();

	static {
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		om.registerModule(new JSR310Module());
		om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
	}

	public static void main(String[] args) throws IOException {
		TemplateTask task1 = new TemplateTask();
		String TASK_1 = "task1";
		String TASK_2 = "task2";

		task1.setId(TASK_1);
		task1.setProcessClassName("task_executor.process.CurlTemplate");
		task1.setInputMappingExpression(TaskExecutionContext.EXP_INIT_PARAMS);
		task1.setOutputMappingExpression(".statusCode");
		task1.setEndLogExpression("\"Finish task 1\"");

		TemplateTask task2 = new TemplateTask();
		task2.setId(TASK_2);
		task2.setProcessClassName("task_executor.process.LogTemplate");
		task2.setInputMappingExpression("{\"severity\": \"INFO\", \"message\": \"Status code is \" + .}");
		task2.setDependencies(Collections.singletonList(TASK_1));

		Map<String, Object> input = ImmutableMap.of(
													"url", "https://example.com",
													"method", "GET"
													);

		CompoundTask mainTask = new CompoundTask(Sets.newHashSet(task1, task2));
		//		mainTask.setOutputMappingExpression("{\"out_1\": .task1.out, \"out_2\": .task2.out}");
		mainTask.setOutputMappingExpression("{}");
		JsonNode output = mainTask.process(Main.om.valueToTree(input));
		System.out.println("Task output:");
		System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(output));
		System.out.println("Task logs");
		System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(mainTask.getLogs()));
	}
}
