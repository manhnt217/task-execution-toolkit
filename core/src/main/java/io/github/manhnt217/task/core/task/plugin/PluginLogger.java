package io.github.manhnt217.task.core.task.plugin;

/**
 * @author manh nguyen
 */
public interface PluginLogger {

    void info(String message);

    void warn(String message, Throwable e);

    void error(String message, Throwable e);
}
