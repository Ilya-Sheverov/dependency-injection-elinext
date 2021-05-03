package ilya.sheverov.dependencyinjectorelinext.injector;

import ilya.sheverov.dependencyinjectorelinext.provider.Provider;

public interface Injector {

    <T> Provider<T> getProvider(Class<T> type);

    <T> void bind(Class<T> intf, Class<? extends T> impl);

    <T> void bindSingleton(Class<T> intf, Class<? extends T> impl);

}
