package ilya.sheverov.dependencyinjectorelinext.exception;

/**
 * Требовалось реализовать в тестовом задании. Ошибка возникает тогда, когда найдено больше одного подходящего
 * конструтора.
 */
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
