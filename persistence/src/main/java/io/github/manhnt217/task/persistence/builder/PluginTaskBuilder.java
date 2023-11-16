package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.task.PluginTask;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author manhnguyen
 */
public class PluginTaskBuilder extends AbstractTaskBuilder<PluginTask> {

    PluginTaskBuilder(String name) {
        this.name = name;
    }

    @Override
    public PluginTask build() {
        return loadTask(this.name);
    }

    public static PluginTask loadTask(String taskClass)  {
        try {
            Class<?> clazz = Class.forName(taskClass);
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
