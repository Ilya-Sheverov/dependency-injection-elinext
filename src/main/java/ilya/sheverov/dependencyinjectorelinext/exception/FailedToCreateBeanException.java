package ilya.sheverov.dependencyinjectorelinext.exception;

/**
 * Ошибка говорит о том, что в ходе выполнения програмы произошла ошбка и не удалось создать bean.
 */
public class FailedToCreateBeanException extends RuntimeException {

    public FailedToCreateBeanException() {
        super();
    }

    public FailedToCreateBeanException(String message) {
        super(message);
    }

    public FailedToCreateBeanException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedToCreateBeanException(Throwable cause) {
        super(cause);
    }

    protected FailedToCreateBeanException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
