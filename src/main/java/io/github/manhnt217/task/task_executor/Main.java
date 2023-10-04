package io.github.manhnt217.task.task_executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.github.manhnt217.task.task_executor.executor.TaskExecutionException;
import io.github.manhnt217.task.task_executor.executor.TaskExecutor;
import io.github.manhnt217.task.task_executor.process.DefaultLogger;
import io.github.manhnt217.task.task_executor.task.CompoundTask;
import io.github.manhnt217.task.task_executor.task.Task;
import io.github.manhnt217.task.task_executor.task.TemplateTask;

import java.io.IOException;
import java.util.*;

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

		try {
			executeSimpleTask();
		} catch (Exception e) {
		}

		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println(" ---------------------- TEST 2 ---------------------- ");

		executeComplexTask();
	}

	private static void executeSimpleTask() throws JsonProcessingException, TaskExecutionException {
		DefaultLogger logHandler = new DefaultLogger();

		Map<String, Object> input = ImmutableMap.of(
				"address", "https://example.com",
				"http-method", "GET"
		);

		TemplateTask task1 = new TemplateTask();
		task1.setTaskName("task1");
		task1.setTemplateName("CurlTemplate");
		task1.setEndLogExpression("\"Finish task 1\"");
		task1.setInputMappingExpression("{\"uurl\": ._PARENT_.address, \"method\": ._PARENT_.\"http-method\"}");

		CompoundTask task = buildTaskWrapper(task1);
		TaskExecutor executor = TaskExecutor.getTaskExecutor(task);
		JsonNode output = null;
		String executionSessionId = UUID.randomUUID().toString();
		try {
			output = executor.execute(task, TaskExecutor.om.valueToTree(input), executionSessionId, logHandler);
		} catch (Exception e) {
			logHandler.error(executionSessionId, task.getTaskName(), "", e);
		}
		System.out.println("Task output:");
		System.out.println(TaskExecutor.om.writerWithDefaultPrettyPrinter().writeValueAsString(output));
		System.out.println("Task logs");
		System.out.println(TaskExecutor.om.writerWithDefaultPrettyPrinter().writeValueAsString(logHandler.getLogs()));
	}

	private static CompoundTask buildTaskWrapper(Task task) {
		CompoundTask wrapper = new CompoundTask(Collections.singletonList(task));
		wrapper.setTaskName("WRAPPER-" + UUID.randomUUID());
		return wrapper;
	}

	private static void executeComplexTask() throws JsonProcessingException, TaskExecutionException {
		DefaultLogger logHandler = new DefaultLogger();

		TemplateTask task1 = new TemplateTask();
		task1.setTaskName("task1");
		task1.setInputMappingExpression("._PARENT_");
		task1.setTemplateName("CurlTemplate");
		task1.setEndLogExpression("\"Finish task 1\"");

		TemplateTask task2 = new TemplateTask();
		task2.setTaskName("task2");
		task2.setTemplateName("LogTemplate");
		task2.setInputMappingExpression("{\"severity\": \"INFO\", \"message\": \"Status code is \\n\" + .task1.statusCode}");

		TemplateTask task3 = new TemplateTask();
		task3.setTaskName("task3");
		task3.setTemplateName("SqlTemplate");
		task3.setInputMappingExpression("{\"sql\":\"" + SQL + "\",\"dataSource\":\"abdc\"}");

		task2.setDependencies(Sets.newHashSet(task1.getTaskName()));
		task3.setDependencies(Sets.newHashSet(task2.getTaskName()));

		CompoundTask compoundTask1 = new CompoundTask(Lists.newArrayList(task1, task3, task2));
		compoundTask1.setInputMappingExpression("._PARENT_");
		compoundTask1.setTaskName("c1");

		CompoundTask compoundTask2 = new CompoundTask(Lists.newArrayList(task1, task2, task3));
		compoundTask2.setInputMappingExpression("._PARENT_");
		compoundTask2.setTaskName("c2");

		CompoundTask mainTask = new CompoundTask(Lists.newArrayList(compoundTask1, compoundTask2));
		mainTask.setInputMappingExpression("._PARENT_");
		mainTask.setTaskName("main");

		Map<String, Object> input = ImmutableMap.of(
				"url", "https://example.com",
				"method", "GET"
		);
		CompoundTask task = buildTaskWrapper(mainTask);
		TaskExecutor executor = TaskExecutor.getTaskExecutor(task);
		JsonNode output = executor.execute(task, TaskExecutor.om.valueToTree(input), UUID.randomUUID().toString(), logHandler);
		System.out.println("Task output:");
		System.out.println(TaskExecutor.om.writerWithDefaultPrettyPrinter().writeValueAsString(output));
		System.out.println("Task logs");
		System.out.println(TaskExecutor.om.writerWithDefaultPrettyPrinter().writeValueAsString(logHandler.getLogs()));
	}
}
