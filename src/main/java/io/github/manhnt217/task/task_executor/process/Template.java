package io.github.manhnt217.task.task_executor.process;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.Main;
import io.github.manhnt217.task.task_executor.process.builtin.CurlTemplate;
import io.github.manhnt217.task.task_executor.process.builtin.LogTemplate;
import io.github.manhnt217.task.task_executor.process.builtin.SqlTemplate;
import io.github.manhnt217.task.task_executor.task.TaskExecutionException;

public abstract class Template<P, R> {

	// TODO: Load process based on className (reflection) will be added later.
	public static JsonNode run(String templateClassName, JsonNode input, LogHandler log) throws TemplateExecutionException {
		switch (templateClassName) {
			case "CurlTemplate":
				return new CurlTemplate().run(input, log);
			case "LogTemplate":
				return new LogTemplate().run(input, log);
			case "SqlTemplate":
				return new SqlTemplate().run(input, log);
			default:
				throw new IllegalArgumentException("Template not found: " + templateClassName);
		}
	}

	public final JsonNode run(JsonNode inputJS, LogHandler log) throws TemplateExecutionException {
		P input;
		try {
			input = Main.om.treeToValue(inputJS, getInputClass());
		} catch (JsonProcessingException e) {
			log.log(Severity.ERROR, "Cannot convert input");
			throw new TaskExecutionException("Cannot convert input. Process stop", e);
		}
		R rs = null;
		try {
			rs = exec(input, log);
		} catch (Exception e) {
			throw new TemplateExecutionException(e);
		}
		return Main.om.valueToTree(rs);
	}

	// TODO: Use reflection to avoid implementing this method
	protected abstract Class<? extends P> getInputClass();

	public abstract R exec(P input, LogHandler log);
}
