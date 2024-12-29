package io.github.manhnt217.task.task_executor.process;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.Main;
import io.github.manhnt217.task.task_executor.task.TaskExecutionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class Template<P, R> {

	private static final String BUILTIN_TEMPLATE_PACKAGE = Template.class.getPackage().getName() + ".builtin.";

	public static JsonNode run(String templateName, JsonNode input, LogHandler log) throws TemplateExecutionException {
		try {
			String className = BUILTIN_TEMPLATE_PACKAGE + templateName;
			Class<?> clazz = Class.forName(className);
			if (!Template.class.isAssignableFrom(clazz)) {
				throw new TemplateExecutionException("Template class " + className + " does not extend " + Template.class.getName());
			}

			Method runMethod = clazz.getMethod("run", JsonNode.class, LogHandler.class);
			Object templateInstance = clazz.newInstance();
			Object result = runMethod.invoke(templateInstance, input, log);
			return Main.om.valueToTree(result);
		} catch (ClassNotFoundException e) {
			throw new TemplateExecutionException("Could not find any builtin template with name: " + templateName);
		} catch (NoSuchMethodException e) {
			throw new TemplateExecutionException("Could not find method run(JsonNode, LogHandler) in template class: " + templateName);
		} catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
			throw new TemplateExecutionException("Could not execute method run(JsonNode, LogHandler) in template class: " + templateName, e);
        }
    }

	@SuppressWarnings("unused")
	public final JsonNode run(JsonNode inputJS, LogHandler logHandler) throws TemplateExecutionException {
		P input;
		try {
			input = Main.om.treeToValue(inputJS, getInputClass());
		} catch (JsonProcessingException e) {
			logHandler.log(Severity.ERROR, "Cannot convert input");
			throw new TaskExecutionException("Cannot convert input. Process stop", e);
		}
		try {
			R rs = exec(input, logHandler);
			return Main.om.valueToTree(rs);
		} catch (Exception e) {
			throw new TemplateExecutionException(e);
		}
	}

	protected abstract Class<? extends P> getInputClass();

	public abstract R exec(P input, LogHandler logHandler);
}
