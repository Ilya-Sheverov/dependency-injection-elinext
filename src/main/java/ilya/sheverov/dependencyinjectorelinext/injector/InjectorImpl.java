package ilya.sheverov.dependencyinjectorelinext.injector;

import ilya.sheverov.dependencyinjectorelinext.exception.IllegalArgumentForBindingException;
import ilya.sheverov.dependencyinjectorelinext.provider.Provider;

import java.lang.reflect.Modifier;

public class InjectorImpl implements Injector {

    BindingBeansContext bindingBeansContext = new BindingBeansContext();

    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        if (type.isInterface()) {
            if (bindingBeansContext.hasBinding(type)) {
                return () -> bindingBeansContext.getBean(type);
            }
            return null;
        } else {
            throw new IllegalArgumentForBindingException("You can't pass non-interfaces.");
        }
    }

    @Override
    public <T> void bind(Class<T> intf, Class<? extends T> impl) {
        if (intf.isInterface()) {
            if (!Modifier.isAbstract(impl.getModifiers())) {
                bindingBeansContext.bind(intf, impl, false);
            } else {
                throw new IllegalArgumentForBindingException("You can't pass abstract classes.");
            }
        } else {
            throw new IllegalArgumentForBindingException("You can't pass non-interfaces.");
        }
    }

    @Override
    public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {
        if (intf.isInterface()) {
            if (!Modifier.isAbstract(impl.getModifiers())) {
                bindingBeansContext.bind(intf, impl, true);
            } else {
                throw new IllegalArgumentForBindingException("You can't pass abstract classes.");
            }
        } else {
            throw new IllegalArgumentForBindingException("You can't pass non-interfaces.");
        }
    }
}
