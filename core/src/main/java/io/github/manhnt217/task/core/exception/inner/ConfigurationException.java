package io.github.manhnt217.task.core.exception.inner;

/**
 * @author manh nguyen
 */
public class ConfigurationException extends Exception {
    public ConfigurationException(String message, Throwable cause) {
        super("Configuration failed. Message = " + message, cause);
    }

    public ConfigurationException(String message) {
        super("Configuration failed. Message = " + message);
    }
}
