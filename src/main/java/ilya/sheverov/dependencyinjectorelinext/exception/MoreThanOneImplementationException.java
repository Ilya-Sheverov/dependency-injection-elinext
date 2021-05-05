package ilya.sheverov.dependencyinjectorelinext.exception;

public class MoreThanOneImplementationException extends RuntimeException {

    public MoreThanOneImplementationException() {
        super();
    }

    public MoreThanOneImplementationException(String message) {
        super(message);
    }

    public MoreThanOneImplementationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MoreThanOneImplementationException(Throwable cause) {
        super(cause);
    }

    protected MoreThanOneImplementationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
