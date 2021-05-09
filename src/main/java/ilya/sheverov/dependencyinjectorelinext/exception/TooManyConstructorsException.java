package ilya.sheverov.dependencyinjectorelinext.exception;

/**
 * Требовалось реализовать в тестовом задании.
 * <p>
 * Ошибка возникает тогда, когда найдено больше одного подходящего конструктора.
 *
 * @author Ilya Sheverov
 * @see ilya.sheverov.dependencyinjectorelinext.bean.constructor.ConstructorDeterminant#getConstructorForInjection(Class)
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

    protected TooManyConstructorsException(String message, Throwable cause,
        boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
