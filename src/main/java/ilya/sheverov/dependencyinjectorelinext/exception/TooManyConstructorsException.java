package ilya.sheverov.dependencyinjectorelinext.exception;

public class TooManyConstructorsException extends RuntimeException {

    public TooManyConstructorsException() {
        super();
    }

    public TooManyConstructorsException(String message) {
        super(message);
    }

    public TooManyConstructorsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TooManyConstructorsException(Throwable cause) {
        super(cause);
    }

    protected TooManyConstructorsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
