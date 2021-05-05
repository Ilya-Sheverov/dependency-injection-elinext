package ilya.sheverov.dependencyinjectorelinext.injector;

import ilya.sheverov.dependencyinjectorelinext.exception.BindingNotFoundException;
import ilya.sheverov.dependencyinjectorelinext.exception.FailedToCreateBeanException;
import ilya.sheverov.dependencyinjectorelinext.exception.IllegalArgumentForBindingException;
import ilya.sheverov.dependencyinjectorelinext.exception.MoreThanOneImplementationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BindingBeansContext {

    private final BeanDefinitionFactory beanDefinitionFactory = new BeanDefinitionFactoryImpl();

    private final Map<Class<?>, BeanDefinition> beansDefinitionStorage = new HashMap<>();

    private final Map<Class<?>, Object> singletonContainer = new ConcurrentHashMap<>();

    Lock lock = new ReentrantLock();

    private <T> T getPrototypeBean(BeanDefinition beanDefinition) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> typeOfBean = beanDefinition.getTypeOfBean();
        int constructorParametersCount = beanDefinition.getConstructorParametersCount();
        Object[] constructorArgsValues = new Object[constructorParametersCount];
        Class<?>[] constructorParametersTypes = beanDefinition.getConstructorParametersTypes();
        if (constructorParametersCount == 0) {
            return (T) typeOfBean.getConstructor(constructorParametersTypes).newInstance(constructorArgsValues);
        } else {
            int constructorArgNumber = 0;
            for (Class<?> constructorParameterType : constructorParametersTypes) {
                constructorArgsValues[constructorArgNumber] = getBean(constructorParameterType);
                constructorArgNumber++;
            }
            return (T) typeOfBean.getConstructor(constructorParametersTypes).newInstance(constructorArgsValues);
        }
    }

    private <T> T getSingletonBean(BeanDefinition beanDefinition, Class<?> intf) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (singletonContainer.containsKey(intf)) {
            return (T) singletonContainer.get(intf);
        } else {
            lock.lock();
            try {
                Object bean = getPrototypeBean(beanDefinition);
                singletonContainer.put(intf, bean);
                return (T) bean;
            } finally {
                lock.unlock();
            }
        }
    }

    public <T> T getBean(Class<T> type) {
        if (type.isInterface()) {
            if (beansDefinitionStorage.containsKey(type)) {
                BeanDefinition beanDefinition = beansDefinitionStorage.get(type);
                if (beanDefinition.isSingleton()) {
                    try {
                        return getSingletonBean(beanDefinition, type);
                    } catch (Exception e) {
                        throw new FailedToCreateBeanException(e);
                    }
                } else {
                    try {
                        return getPrototypeBean(beanDefinition);
                    } catch (BindingNotFoundException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new FailedToCreateBeanException(e);
                    }
                }
            } else {
                throw new BindingNotFoundException("No building found for the type " + type);
            }
        }
        throw new IllegalArgumentForBindingException("You can't pass non-interfaces.");
    }

    public <T> void bind(Class<T> intf, Class<? extends T> impl, boolean isSingleton) {
        if (intf.isInterface()) {
            if (!Modifier.isAbstract(impl.getModifiers())) {
                if (!beansDefinitionStorage.containsKey(intf)) {
                    BeanDefinition beanDefinition = beanDefinitionFactory.getBeanDefinition(impl, isSingleton);
                    beansDefinitionStorage.put(intf, beanDefinition);
                } else {
                    throw new MoreThanOneImplementationException("More than one implementation detected " + intf.getClasses());
                }
            } else {
                throw new IllegalArgumentForBindingException("You can't pass abstract classes.");
            }
        } else {
            throw new IllegalArgumentForBindingException("You can't pass non-interfaces.");
        }
    }

    public boolean hasBinding(Class<?> type) {
        return beansDefinitionStorage.containsKey(type);
    }
}
