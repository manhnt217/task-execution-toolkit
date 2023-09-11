package io.github.manhnt217.task.task_executor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import io.github.manhnt217.task.task_executor.executor.CompoundTaskExecutor;
import io.github.manhnt217.task.task_executor.executor.TaskExecutor;
import io.github.manhnt217.task.task_executor.task.CompoundTask;
import io.github.manhnt217.task.task_executor.task.Task;
import io.github.manhnt217.task.task_executor.task.TaskExecutionContext;
import io.github.manhnt217.task.task_executor.task.TemplateTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Map;

public class Main {

	public static final ObjectMapper om = new ObjectMapper();
	public static final String SQL = "DECLARE " +
			"    p varchar2(10); " +
			"BEGIN " +
			"    select DUMMY into p from dual; " +
			"    insert into TESTPLSQL(RECORD_NAME) values ('Hell o112 PL/SQL');" +
			"    DBMS_OUTPUT.PUT_LINE('Got a result: ================ ' || p || chr(10) || ' ABC'); " +
			"END;";

	static {
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		om.registerModule(new JSR310Module());
		om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
	}

	public static void main(String[] args) throws IOException {

		TemplateTask task1 = new TemplateTask();
		task1.setId("task1");
		task1.setTemplateName("CurlTemplate");
		task1.setInputMappingExpression(TaskExecutionContext.EXP_INIT_PARAMS);
		task1.setInputType(Task.InputType.CONTEXT);
		task1.setOutputMappingExpression(".statusCode");
		task1.setEndLogExpression("\"Finish task 1\"");

		TemplateTask task2 = new TemplateTask();
		task2.setId("task2");
		task2.setTemplateName("LogTemplate");
		task2.setInputType(Task.InputType.PREVIOUS_TASK);
		task2.setInputMappingExpression("{\"severity\": \"INFO\", \"message\": \"Status code is \\n\" + .}");

		TemplateTask task3 = new TemplateTask();
		task3.setId("task3");
		task3.setTemplateName("SqlTemplate");
		task3.setInputType(Task.InputType.CONTEXT);
		task3.setInputMappingExpression("{\"sql\":\"" + SQL + "\",\"dataSource\":\"abdc\"}");

		task2.setDependencies(Sets.newHashSet(task1.getId()));
		task3.setDependencies(Sets.newHashSet(task2.getId()));

		Map<String, Object> input = ImmutableMap.of(
													"url", "https://example.com",
													"method", "GET"
													);

		CompoundTask compoundTask1 = new CompoundTask(Sets.newHashSet(task1, task3, task2));
		compoundTask1.setId("c1");
		compoundTask1.setInputType(Task.InputType.CONTEXT);
		compoundTask1.setInputMappingExpression(TaskExecutionContext.EXP_INIT_PARAMS);

		CompoundTask compoundTask2 = new CompoundTask(Sets.newHashSet(task1, task2, task3));
		compoundTask2.setId("c2");
		compoundTask2.setInputType(Task.InputType.CONTEXT);
		compoundTask2.setInputMappingExpression(TaskExecutionContext.EXP_INIT_PARAMS);

		CompoundTask mainTask = new CompoundTask(Sets.newHashSet(compoundTask1, compoundTask2));

		// FIXME: If a task executed more than once, the logs will add up.
//		mainTask.setOutputMappingExpression("{\"out_1\": .task1.out, \"out_2\": .task2.out}");
		mainTask.setOutputMappingExpression("{}");


		JsonNode input1 = Main.om.valueToTree(input);
		TaskExecutor executor = TaskExecutor.getTaskExecutor(mainTask);
		JsonNode output = executor.execute(mainTask, input1);
		System.out.println("Task output:");
		System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(output));
		System.out.println("Task logs");
		System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(executor.getLogs()));
	}
}
