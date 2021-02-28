package httpserver.config.exceptions;

public class InvalidHeaderException extends RuntimeException {

    /*
    This exception is thrown when there was problem with reading the header of the request
     */

    public InvalidHeaderException() {

    }

    public InvalidHeaderException(String message) {
        super(message);
    }

    public InvalidHeaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidHeaderException(Throwable cause) {
        super(cause);
    }


}
