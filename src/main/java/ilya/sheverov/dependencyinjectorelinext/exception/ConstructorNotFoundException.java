package ilya.sheverov.dependencyinjectorelinext.exception;

/**
 * Требовалось реализовать в тестовом задании. Ошибка говрит о том, что конструктор, необходимый для создания объекта
 * через интерфейс {@link ilya.sheverov.dependencyinjectorelinext.injector.Injector} не найден.
 */
public class ConstructorNotFoundException extends RuntimeException {

    public ConstructorNotFoundException() {
        super();
    }

    public ConstructorNotFoundException(String message) {
        super(message);
    }

    public ConstructorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstructorNotFoundException(Throwable cause) {
        super(cause);
    }

    protected ConstructorNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
