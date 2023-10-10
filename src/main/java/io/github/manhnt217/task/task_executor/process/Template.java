package io.github.manhnt217.task.task_executor.process;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.manhnt217.task.task_executor.task.Task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class Template<P, R> {

    public static final String EXEC_METHOD_NAME = "run";
    private static final String BUILTIN_TEMPLATE_PACKAGE = Template.class.getPackage().getName() + ".builtin.";

    public static JsonNode run(String templateName, JsonNode input, TemplateLogger log) throws TemplateExecutionException {
        try {
            String className = BUILTIN_TEMPLATE_PACKAGE + templateName;
            Class<?> clazz = Class.forName(className);
            if (!Template.class.isAssignableFrom(clazz)) {
                throw new TemplateExecutionException(templateName, "Template class does not extend " + Template.class.getName());
            }

            Method runMethod = clazz.getMethod(EXEC_METHOD_NAME, JsonNode.class, TemplateLogger.class);
            Object templateInstance = clazz.newInstance();
            Object result = runMethod.invoke(templateInstance, input, log);
            return Task.OBJECT_MAPPER.valueToTree(result);
        } catch (ClassNotFoundException e) {
            throw new TemplateExecutionException(templateName, "Could not find any builtin template");
        } catch (NoSuchMethodException e) {
            throw new TemplateExecutionException(templateName, "Could not find method run(JsonNode, LogHandler) in template");
        } catch (InstantiationException e) {
            throw new TemplateExecutionException(templateName, "Could not create a new instance of template");
        } catch (IllegalAccessException e) {
            throw new TemplateExecutionException(templateName, "Could not access method run(JsonNode, LogHandler) in template instance");
        } catch (InvocationTargetException e) {
            throw new TemplateExecutionException(templateName, input, e.getTargetException());
        } catch (Exception e) {
            throw new TemplateExecutionException(templateName, input, e);
        }
    }

    @SuppressWarnings("unused")
    public final JsonNode run(JsonNode inputJS, TemplateLogger logHandler) throws Exception {
        R rs = exec(Task.OBJECT_MAPPER.treeToValue(inputJS, getInputClass()), logHandler);
        return Task.OBJECT_MAPPER.valueToTree(rs);
    }

    protected abstract Class<? extends P> getInputClass();

    public abstract R exec(P input, TemplateLogger logger) throws Exception;
}
