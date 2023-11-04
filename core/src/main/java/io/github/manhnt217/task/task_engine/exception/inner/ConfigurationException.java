package io.github.manhnt217.task.task_engine.exception.inner;

/**
 * @author manhnguyen
 */
public class ConfigurationException extends Exception {
    public ConfigurationException(String message, Throwable cause) {
        super("Configuration failed. Message = " + message, cause);
    }

    public ConfigurationException(String message) {
        super("Configuration failed. Message = " + message);
    }
}
