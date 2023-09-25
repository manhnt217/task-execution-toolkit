package io.github.manhnt217.task.task_executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.github.manhnt217.task.task_executor.executor.JSLTUtil;
import io.github.manhnt217.task.task_executor.executor.TaskExecutionException;
import io.github.manhnt217.task.task_executor.executor.TaskExecutor;
import io.github.manhnt217.task.task_executor.process.ExecutionLog;
import io.github.manhnt217.task.task_executor.process.LogHandler;
import io.github.manhnt217.task.task_executor.process.Severity;
import io.github.manhnt217.task.task_executor.task.CompoundTask;
import io.github.manhnt217.task.task_executor.task.Task;
import io.github.manhnt217.task.task_executor.task.TemplateTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class Main {

	public static final String SQL = "DECLARE " +
			"    p varchar2(10); " +
			"BEGIN " +
			"    select DUMMY into p from dual; " +
			"    insert into TESTPLSQL(RECORD_NAME, CREATED) values ('Hell o112 PL/SQL', systimestamp);" +
			"    DBMS_OUTPUT.PUT_LINE('Got a result: ================ ' || p || chr(10) || ' ABC'); " +
			"END;";

	public static void main(String[] args) throws IOException, TaskExecutionException {
		System.out.println(" ---------------------- TEST 1 ---------------------- ");

		executeSimpleTask();

		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println(" ---------------------- TEST 2 ---------------------- ");

		executeComplexTask();
	}

	private static void executeSimpleTask() throws JsonProcessingException, TaskExecutionException {
		ArrayList<ExecutionLog> logs = new ArrayList<>();
		LogHandler logHandler = (executionSessionId, taskId, severity, message) -> logs.add(new ExecutionLog(executionSessionId, taskId, severity, message));

		Map<String, Object> input = ImmutableMap.of(
				"address", "https://example.com",
				"http-method", "GET"
		);

		TemplateTask task1 = new TemplateTask();
		task1.setId("task1");
		task1.setTemplateName("CurlTemplate");
		task1.setInputType(Task.InputType.PARENT);
		task1.setEndLogExpression("\"Finish task 1\"");
		task1.setInputMappingExpression("{\"url\": .address, \"method\": .\"http-method\"}");

		JsonNode taskInput = JSLTUtil.applyTransform(task1.getInputMappingExpression(), TaskExecutor.om.valueToTree(input));
		TaskExecutor executor = TaskExecutor.getTaskExecutor(task1);
		JsonNode output = executor.execute(task1, taskInput, UUID.randomUUID().toString(), logHandler);
		System.out.println("Task output:");
		System.out.println(TaskExecutor.om.writerWithDefaultPrettyPrinter().writeValueAsString(output));
		System.out.println("Task logs");
		System.out.println(TaskExecutor.om.writerWithDefaultPrettyPrinter().writeValueAsString(logs));
	}

	private static void executeComplexTask() throws JsonProcessingException, TaskExecutionException {
		ArrayList<ExecutionLog> logs = new ArrayList<>();
		LogHandler logHandler = (executionSessionId, taskId, severity, message) -> logs.add(new ExecutionLog(executionSessionId, taskId, severity, message));

		TemplateTask task1 = new TemplateTask();
		task1.setId("task1");
		task1.setTemplateName("CurlTemplate");
		task1.setInputType(Task.InputType.PARENT);
		task1.setEndLogExpression("\"Finish task 1\"");

		TemplateTask task2 = new TemplateTask();
		task2.setId("task2");
		task2.setTemplateName("LogTemplate");
		task2.setInputType(Task.InputType.PREVIOUS_TASK);
		task2.setInputMappingExpression("{\"severity\": \"INFO\", \"message\": \"Status code is \\n\" + .statusCode}");

		TemplateTask task3 = new TemplateTask();
		task3.setId("task3");
		task3.setTemplateName("SqlTemplate");
		task3.setInputType(Task.InputType.PARENT);
		task3.setInputMappingExpression("{\"sql\":\"" + SQL + "\",\"dataSource\":\"abdc\"}");

		task2.setDependencies(Sets.newHashSet(task1.getId()));
		task3.setDependencies(Sets.newHashSet(task2.getId()));

		CompoundTask compoundTask1 = new CompoundTask(Lists.newArrayList(task1, task3, task2));
		compoundTask1.setId("c1");
		compoundTask1.setInputType(Task.InputType.PARENT);

		CompoundTask compoundTask2 = new CompoundTask(Lists.newArrayList(task1, task2, task3));
		compoundTask2.setId("c2");
		compoundTask2.setInputType(Task.InputType.PARENT);

		CompoundTask mainTask = new CompoundTask(Lists.newArrayList(compoundTask1, compoundTask2));

		Map<String, Object> input = ImmutableMap.of(
				"url", "https://example.com",
				"method", "GET"
		);
		JsonNode input1 = TaskExecutor.om.valueToTree(input);
		TaskExecutor executor = TaskExecutor.getTaskExecutor(mainTask);
		JsonNode output = executor.execute(mainTask, input1, UUID.randomUUID().toString(), logHandler);
		System.out.println("Task output:");
		System.out.println(TaskExecutor.om.writerWithDefaultPrettyPrinter().writeValueAsString(output));
		System.out.println("Task logs");
		System.out.println(TaskExecutor.om.writerWithDefaultPrettyPrinter().writeValueAsString(logs));
	}
}
