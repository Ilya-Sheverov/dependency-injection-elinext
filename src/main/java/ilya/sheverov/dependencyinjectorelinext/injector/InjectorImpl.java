package ilya.sheverov.dependencyinjectorelinext.injector;

import ilya.sheverov.dependencyinjectorelinext.bean.BeanDefinition;
import ilya.sheverov.dependencyinjectorelinext.bean.BeanDefinitionFactory;
import ilya.sheverov.dependencyinjectorelinext.exception.*;
import ilya.sheverov.dependencyinjectorelinext.provider.Provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Реализация интерфейса {@link Injector}, которая требовалась в тестовом задании.
 * <p>
 * Основной класс для настройки всего Dependency Injection-контейнера.
 * InjectorImpl содержит фабрику для создания объектов BeanDefinition {@link BeanDefinitionFactory}.
 * Хранит все созданные во время байдинга BeanDefinition в {@code beansDefinitionStorage}.
 * Хранит созданные синглтон бины в {@code singletonContainer}.
 * Содержит методы для привязки и создания бинов.
 *
 * @author Ilya Sheverov
 * @see #bind(Class, Class)
 * @see #bindSingleton(Class, Class)
 * @see #getBean(Class)
 * @since 1.0
 */
public class InjectorImpl implements Injector {

    private final BeanDefinitionFactory beanDefinitionFactory = new BeanDefinitionFactory();
    private final Map<Class<?>, BeanDefinition> beansDefinitionStorage = new HashMap<>();
    private final Map<Class<?>, Class<?>> interfaceByAClass = new HashMap<>();
    private final Map<Class<?>, Object> singletonContainer = new ConcurrentHashMap<>();
    private Lock lock = new ReentrantLock();

    private Object getPrototypeBean(BeanDefinition beanDefinition) throws IllegalAccessException,
        InvocationTargetException, InstantiationException {
        int constructorParametersCount = beanDefinition.getConstructorParametersCount();
        Object[] constructorArgsValues = new Object[constructorParametersCount];
        Class<?>[] constructorParametersTypes = beanDefinition.getConstructorParametersTypes();
        if (constructorParametersCount == 0) {
            Constructor<?> constructor = beanDefinition.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance(constructorArgsValues);
        } else {
            int constructorArgNumber = 0;
            for (Class<?> constructorParameterType : constructorParametersTypes) {
                constructorArgsValues[constructorArgNumber] = getBean(constructorParameterType);
                constructorArgNumber++;
            }
            Constructor<?> constructor = beanDefinition.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance(constructorArgsValues);
        }
    }

    private Object getSingletonBean(BeanDefinition beanDefinition, Class<?> intf) throws InvocationTargetException,
        InstantiationException, IllegalAccessException {
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

    private Object getBean(Class<?> type) {
        if (hasBinding(type)) {
            BeanDefinition beanDefinition = getBeanDefinitionByType(type);
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

    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        if (type.isInterface()) {
            if (hasBinding(type)) {
                return () -> (T) getBean(type);
            }
            return null;
        } else {
            throw new IllegalArgumentForBindingException("You can't pass non-interfaces.");
        }
    }

    public <T> void bind(Class<T> intf, Class<? extends T> impl, boolean isSingleton) {
        if (intf.isInterface()) {
            if (!Modifier.isAbstract(impl.getModifiers())) {
                if (!beansDefinitionStorage.containsKey(intf)) {
                    BeanDefinition beanDefinition = beanDefinitionFactory.getBeanDefinition(impl, isSingleton);
                    beansDefinitionStorage.put(intf, beanDefinition);
                    interfaceByAClass.put(impl, intf);
                } else {
                    throw new MoreThanOneImplementationException("More than one implementation detected " + intf);
                }
            } else {
                throw new IllegalArgumentForBindingException("You can't pass abstract classes.");
            }
        } else {
            throw new IllegalArgumentForBindingException("You can't pass non-interfaces.");
        }
    }

    @Override
    public <T> void bind(Class<T> intf, Class<? extends T> impl) {
        bind(intf, impl, false);
    }

    @Override
    public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {
        bind(intf, impl, true);
    }

    public boolean hasBinding(Class<?> type) {
        if (type.isInterface()) {
            return beansDefinitionStorage.containsKey(type);
        } else {
            return interfaceByAClass.containsKey(type);
        }
    }

    public BeanDefinition getBeanDefinitionByType(Class<?> type) {
        if (type.isInterface()) {
            return beansDefinitionStorage.get(type);
        } else {
            return beansDefinitionStorage.get(interfaceByAClass.get(type));
        }
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
        beansDefinitionStorage.forEach(this::findCyclicDependencies);
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
