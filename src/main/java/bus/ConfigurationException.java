package bus;

/**
 * This exception indicates that the classes given to the EventBus are not properly
 * configured.
 */
public class ConfigurationException extends RuntimeException {
    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
