package ilya.sheverov.dependencyinjectorelinext.exception;

public class InvalidConstructorParameterTypeException extends RuntimeException {

    public InvalidConstructorParameterTypeException() {
        super();
    }

    public InvalidConstructorParameterTypeException(String message) {
        super(message);
    }

    public InvalidConstructorParameterTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidConstructorParameterTypeException(Throwable cause) {
        super(cause);
    }

    protected InvalidConstructorParameterTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
