package ilya.sheverov.dependencyinjectorelinext.injector;

import ilya.sheverov.dependencyinjectorelinext.provider.Provider;

/**
 * Требовалось создать в тестовом задании.
 * <p>
 * Классы, реализующие этот интерфейс, должны предоставлять возможность:Получения инстанса класса со всеми иньекциями
 * по классу интерфейса, регистрации байндинга по классу интерфейса и
 * его реализации, регистрации синглтон класса.
 *
 * @author Ilya Sheverov
 * @since 1.0
 */
public interface Injector {

    <T> Provider<T> getProvider(Class<T> type);

    <T> void bind(Class<T> intf, Class<? extends T> impl);

    <T> void bindSingleton(Class<T> intf, Class<? extends T> impl);

}
