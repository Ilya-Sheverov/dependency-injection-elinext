package ilya.sheverov.dependencyinjectorelinext.exception;


import ilya.sheverov.dependencyinjectorelinext.bean.BindingBeansContext;

/**
 * Ошибка может появится при вызове метода checkBindings() класса
 * {@link BindingBeansContext}. Она говрит о том, что среди
 * добавленых биндингов встречаются циклические зависимости.
 */
public class CyclicDependencyException extends RuntimeException {

    public CyclicDependencyException() {
        super();
    }

    public CyclicDependencyException(String message) {
        super(message);
    }

    public CyclicDependencyException(String message, Throwable cause) {
        super(message, cause);
    }

    public CyclicDependencyException(Throwable cause) {
        super(cause);
    }

    protected CyclicDependencyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
