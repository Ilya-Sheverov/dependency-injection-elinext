package ilya.sheverov.dependencyinjectorelinext.exception;

/**
 * Требовалось реализовать в тестовом задании.
 * Ошибка говорит о том, что тип, объект которого требуется передать в конструктор не найден.
 */
public class BindingNotFoundException extends RuntimeException {

    public BindingNotFoundException() {
        super();
    }

    public BindingNotFoundException(String message) {
        super(message);
    }

    public BindingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BindingNotFoundException(Throwable cause) {
        super(cause);
    }

    protected BindingNotFoundException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
