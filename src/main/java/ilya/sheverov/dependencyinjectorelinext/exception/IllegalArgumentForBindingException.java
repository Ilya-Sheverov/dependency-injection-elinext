package ilya.sheverov.dependencyinjectorelinext.exception;

/**
 * Ошибка говорит о том, что в метод класса {@link ilya.sheverov.dependencyinjectorelinext.injector.InjectorImpl}
 * передан не допустимый аргумент.
 */
public class IllegalArgumentForBindingException extends RuntimeException {

    public IllegalArgumentForBindingException() {
        super();
    }

    public IllegalArgumentForBindingException(String message) {
        super(message);
    }

    public IllegalArgumentForBindingException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalArgumentForBindingException(Throwable cause) {
        super(cause);
    }

    protected IllegalArgumentForBindingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
