package io.github.manhnt217.task.task_executor.activity;

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
