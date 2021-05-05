package ilya.sheverov.dependencyinjectorelinext.injector;

import ilya.sheverov.dependencyinjectorelinext.annotation.Inject;
import ilya.sheverov.dependencyinjectorelinext.provider.Provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class InjectorImpl implements Injector {

    BeanDefinitionFactoryImpl beanDefinitionFactory = new BeanDefinitionFactoryImpl();

    Map<Class<?>, BeanDefinition> beansDefinitionStorage = new HashMap<>();

    private <T> T getBean(Class<T> type) {
        if (type.isInterface()) {
            BeanDefinition beanDefinition = beansDefinitionStorage.get(type);
            if (beanDefinition != null) {
                Class<?> typeOfBean = beanDefinition.getTypeOfBean();
                int constructorParametersCount = beanDefinition.getConstructorParametersCount();
                Object[] constructorArgsValues = new Object[constructorParametersCount];
                Class<?>[] constructorParametersTypes = beanDefinition.getConstructorParametersTypes();
                if (constructorParametersCount == 0) {
                    try {
                        return (T) typeOfBean.getConstructor(constructorParametersTypes).newInstance(constructorArgsValues);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else {
                    int constructorArgNumber = 0;
                    for (Class<?> constructorParameterType : constructorParametersTypes) {
                        constructorArgsValues[constructorArgNumber] = getBean(constructorParameterType);
                    }
                    try {
                        return (T) typeOfBean.getConstructor(constructorParametersTypes).newInstance(constructorArgsValues);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                throw new RuntimeException("BindingNotFoundException");
            }
        }
        throw new RuntimeException("Необходимо передавать интерфейсы.");
    }

    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        if (type.isInterface()) {
            Object o = getBean(type);
            return () -> (T) o;
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public <T> void bind(Class<T> intf, Class<? extends T> impl) {
        if (intf.isInterface()) {
            if (!Modifier.isAbstract(impl.getModifiers())) {
                if (beansDefinitionStorage.get(intf) == null) {
                    BeanDefinition beanDefinition = beanDefinitionFactory.getBeanDefinition(impl, false);
                    beansDefinitionStorage.put(intf, beanDefinition);
                } else {
                    throw new RuntimeException("Нельзя использовать больше одной реализации " + intf.getClasses());
                }
            } else {
                throw new RuntimeException("Нельзя передавать абстрактные классы.");
            }
        } else {
            throw new RuntimeException("Нельзя передавать интерфейсы классы.");
        }

    }

    @Override
    public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {
        bind(intf, impl);
    }
}
