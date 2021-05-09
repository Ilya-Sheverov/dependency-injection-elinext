package ilya.sheverov.dependencyinjectorelinext.exception;

/**
 * Ошибка говорит о том, что уже одна имплементация интерфейса при байдинге была добавлена.
 *
 * @author Ilya Sheverov
 * @see ilya.sheverov.dependencyinjectorelinext.injector.InjectorImpl#bind(Class, Class, boolean)
 */
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

    protected MoreThanOneImplementationException(String message, Throwable cause,
        boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
