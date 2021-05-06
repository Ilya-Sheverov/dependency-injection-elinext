package ilya.sheverov.dependencyinjectorelinext.injector;

import ilya.sheverov.dependencyinjectorelinext.provider.Provider;

/**
 * Требовалось создать в тестовом задании. Классы реализующие этот интерфейс, должны предоставлять возможность,
 * получение инстанса класса со всеми иньекциями по классу интерфейса, регистрация байндинга по классу интерфейса и
 * его реализации, регистрация синглтон класса.
 */
public interface Injector {

    <T> Provider<T> getProvider(Class<T> type);

    <T> void bind(Class<T> intf, Class<? extends T> impl);

    <T> void bindSingleton(Class<T> intf, Class<? extends T> impl);

}
