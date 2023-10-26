package io.github.manhnt217.task.sample;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.manhnt217.task.sample.plugin.LogTask;
import io.github.manhnt217.task.task_engine.activity.Activity;
import io.github.manhnt217.task.task_engine.context.SimpleActivityContext;
import io.github.manhnt217.task.task_engine.exception.ActivityException;
import io.github.manhnt217.task.task_engine.activity.OutboundMessage;
import io.github.manhnt217.task.task_engine.activity.impl.DefaultActivityLogger;
import io.github.manhnt217.task.task_engine.activity.impl.SimpleInboundMessage;
import io.github.manhnt217.task.task_engine.context.AbstractActivityContext;
import io.github.manhnt217.task.task_engine.task.PluginTask;
import io.github.manhnt217.task.task_engine.task.Task;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author manhnguyen
 */
public class TestUtil {

    public static final ObjectMapper OM = new ObjectMapper();

    static {
        OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OM.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static JsonNode executeActivity(Activity activity, JsonNode props, DefaultActivityLogger logger, String executionId) throws ActivityException {

        SimpleActivityContext context = new SimpleActivityContext(executionId, props);

        JsonNode inputAfterTransform;
        try {
            inputAfterTransform = context.transformInput(activity);
        } catch (Exception e) {
            throw new ActivityException(activity, "Exception while transform the input", e);
        }

        OutboundMessage output = activity.process(SimpleInboundMessage.of(inputAfterTransform), logger, context);

        return output.getContent();
    }

    private static final String BUILTIN_TASK_PACKAGE = LogTask.class.getPackage().getName() + ".";

    public static Task loadTask(String taskClass)  {
        try {
            String className = BUILTIN_TASK_PACKAGE + taskClass;
            Class<?> clazz = Class.forName(className);
            if (!PluginTask.class.isAssignableFrom(clazz)) {
                throw new RuntimeException("Task class does not extend " + PluginTask.class.getName());
            }
            Constructor<?> constructor = clazz.getConstructor(String.class);
            return (PluginTask) constructor.newInstance(taskClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find any task with name '" + taskClass + "'");
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException("Cannot find suitable constructor for task '" + taskClass + "'");
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Exception while instantiate task '" + taskClass + "'");
        }
    }
}
