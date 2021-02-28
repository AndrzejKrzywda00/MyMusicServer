package httpserver.config.exceptions;

public class HttpConfigurationException extends RuntimeException {

    /*
    This exception is thrown when there is problem with running configuration from file
     */

    public HttpConfigurationException() {
    }

    public HttpConfigurationException(String message) {
        super(message);
    }

    public HttpConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpConfigurationException(Throwable cause) {
        super(cause);
    }

}
