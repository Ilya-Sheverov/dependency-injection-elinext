package ilya.sheverov.dependencyinjectorelinext.bean;

import ilya.sheverov.dependencyinjectorelinext.exception.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BindingBeansContext {

    private final BeanDefinitionFactory beanDefinitionFactory = new BeanDefinitionFactoryImpl();

    private final Map<Class<?>, BeanDefinition> beansDefinitionStorage = new HashMap<>();

    private final Map<Class<?>, Object> singletonContainer = new ConcurrentHashMap<>();

    private Lock lock = new ReentrantLock();

    private Object getPrototypeBean(BeanDefinition beanDefinition) throws NoSuchMethodException, IllegalAccessException,
        InvocationTargetException, InstantiationException {
        Class<?> typeOfBean = beanDefinition.getTypeOfBean();
        int constructorParametersCount = beanDefinition.getConstructorParametersCount();
        Object[] constructorArgsValues = new Object[constructorParametersCount];
        Class<?>[] constructorParametersTypes = beanDefinition.getConstructorParametersTypes();
        if (constructorParametersCount == 0) {
            Constructor<?> constructor = typeOfBean.getConstructor(constructorParametersTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(constructorArgsValues);
        } else {
            int constructorArgNumber = 0;
            for (Class<?> constructorParameterType : constructorParametersTypes) {
                constructorArgsValues[constructorArgNumber] = getBean(constructorParameterType);
                constructorArgNumber++;
            }
            Constructor<?> constructor = typeOfBean.getConstructor(constructorParametersTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(constructorArgsValues);
        }
    }

    private Object getSingletonBean(BeanDefinition beanDefinition, Class<?> intf) throws InvocationTargetException,
        NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (singletonContainer.containsKey(intf)) {
            return singletonContainer.get(intf);
        } else {
            lock.lock();
            try {
                if (singletonContainer.containsKey(intf)) {
                    return singletonContainer.get(intf);
                }
                Object bean = getPrototypeBean(beanDefinition);
                singletonContainer.putIfAbsent(intf, bean);
                return bean;
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
                        return (T) getSingletonBean(beanDefinition, type);
                    } catch (Exception e) {
                        throw new FailedToCreateBeanException(e);
                    }
                } else {
                    try {
                        return (T) getPrototypeBean(beanDefinition);
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

    public void checkBindings() {
        checkThatAllBindingsExist();
        checkThatThereAreNoCyclicDependencies();
    }

    private void checkThatAllBindingsExist() {
        Collection<BeanDefinition> beanDefinitions = beansDefinitionStorage.values();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (beanDefinition.getConstructorParametersCount() != 0) {
                Class<?>[] constructorParametersTypes = beanDefinition.getConstructorParametersTypes();
                for (Class<?> constructorParameterType : constructorParametersTypes) {
                    if (!beansDefinitionStorage.containsKey(constructorParameterType)) {
                        throw new BindingNotFoundException("No building found for the type " + constructorParameterType);
                    }
                }
            }
        }
    }

    private void checkThatThereAreNoCyclicDependencies() {
        beansDefinitionStorage.forEach((type, beanDefinition) -> {
            findCyclicDependencies(type, beanDefinition);
        });
    }

    private void findCyclicDependencies(Class<?> type, BeanDefinition beanDefinition) {
        Class<?>[] constructorParametersTypes = beanDefinition.getConstructorParametersTypes();
        for (Class<?> constructorParameterType : constructorParametersTypes) {
            if (constructorParameterType.equals(type)) {
                throw new CyclicDependencyException();
            } else {
                BeanDefinition nextBeanDefinition = beansDefinitionStorage.get(constructorParameterType);
                findCyclicDependencies(type, nextBeanDefinition);
            }
        }
    }
}
