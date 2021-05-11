package ilya.sheverov.dependencyinjectorelinext.injector;

import ilya.sheverov.dependencyinjectorelinext.bean.BeanDefinition;
import ilya.sheverov.dependencyinjectorelinext.bean.BeanDefinitionFactory;
import ilya.sheverov.dependencyinjectorelinext.exception.BindingNotFoundException;
import ilya.sheverov.dependencyinjectorelinext.exception.CyclicDependencyException;
import ilya.sheverov.dependencyinjectorelinext.exception.FailedToCreateBeanException;
import ilya.sheverov.dependencyinjectorelinext.exception.IllegalArgumentForBindingException;
import ilya.sheverov.dependencyinjectorelinext.exception.MoreThanOneImplementationException;
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
 * Основной класс для настройки всего Dependency Injection-контейнера. InjectorImpl содержит фабрику
 * для создания объектов BeanDefinition {@link BeanDefinitionFactory}. Хранит все созданные во время
 * байдинга BeanDefinition в {@code beansDefinitionStorage}. Хранит созданные синглтон бины в {@code
 * singletonContainer}. Содержит методы для привязки и создания бинов.
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
    private volatile boolean isBeenValidated;

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

    private Object getSingletonBean(BeanDefinition beanDefinition, Class<?> intf)
        throws InvocationTargetException,
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

    /**
     * Возвращает реализацию интерфейса {@code Provider} для типа, который определяется объектом
     * класса {@code Class}, переданного в параметре.
     *
     * @param type объект класса {@code Class}, для которого надо получить {@code Provider}.
     * @param <T>  тип объекта, который определяется классом {@code type}.
     * @return объект реализующий интерфейс {@code Provider}, для типа {@code T}, если для объекта
     * {@code type} существует биндинг, если такого нет, то возвращает null.
     */
    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        if (type.isInterface()) {
            if (hasBinding(type)) {
                if (!isBeenValidated) {
                    checkTypeOnCyclicDependencies(type);
                }
                return () -> (T) getBean(type);
            }
            return null;
        } else {
            throw new IllegalArgumentForBindingException("You can't pass non-interfaces.");
        }
    }

    private <T> void bind(Class<T> intf, Class<? extends T> impl, boolean isSingleton) {
        if (intf.isInterface()) {
            if (!Modifier.isAbstract(impl.getModifiers())) {
                if (!beansDefinitionStorage.containsKey(intf)) {
                    BeanDefinition beanDefinition = beanDefinitionFactory
                        .getBeanDefinition(impl, isSingleton);
                    beansDefinitionStorage.put(intf, beanDefinition);
                    interfaceByAClass.put(impl, intf);
                    if (isBeenValidated) {
                        isBeenValidated = false;
                    }
                } else {
                    throw new MoreThanOneImplementationException(
                        "More than one implementation detected " + intf);
                }
            } else {
                throw new IllegalArgumentForBindingException("You can't pass abstract classes.");
            }
        } else {
            throw new IllegalArgumentForBindingException("You can't pass non-interfaces.");
        }
    }

    /**
     * Связывает интерфейс и его реализацию, сохраняет в контекст всех биндингов этого объекта,
     * помечая, что объект, переданного класса, должен создаваться каждый раз новый.
     *
     * @param intf объект класса {@code Class}, который определяет тип передаваемого интерфейса.
     * @param impl объект класса {@code Class}, который определяет тип передаваемого класса,
     *             реализующего интерфейс {@code intf}.
     * @param <T>  тип передаваемого интерфейса.
     */
    @Override
    synchronized public <T> void bind(Class<T> intf, Class<? extends T> impl) {
        bind(intf, impl, false);
    }

    /**
     * Связывает интерфейс и его реализацию, сохраняет в контекст всех биндингов этого объекта,
     * помечая, что объект, переданного класса, должен создаваться один раз.
     *
     * @param intf объект класса {@code Class}, который определяет тип передаваемого интерфейса.
     * @param impl объект класса {@code Class}, который определяет тип передаваемого класса,
     *             реализующего интерфейс {@code intf}.
     * @param <T>  тип передаваемого интерфейса.
     */
    @Override
    synchronized public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {
        bind(intf, impl, true);
    }

    private boolean hasBinding(Class<?> type) {
        if (type.isInterface()) {
            return beansDefinitionStorage.containsKey(type);
        } else {
            return interfaceByAClass.containsKey(type);
        }
    }

    private BeanDefinition getBeanDefinitionByType(Class<?> type) {
        if (type.isInterface()) {
            return beansDefinitionStorage.get(type);
        } else {
            return beansDefinitionStorage.get(interfaceByAClass.get(type));
        }
    }

    /**
     * Проверяет, что все биндинги, необходимые для создания бинов, добавлены в контекст этого
     * объекта и что среди бинов нет циклических зависимостей.
     *
     * @see #bind(Class, Class)
     * @see #bindSingleton(Class, Class)
     * @see #getProvider(Class)
     */
    synchronized public void checkBindings() {
        isBeenValidated = true;
        checkThatAllBindingsExist();
        checkThatThereAreNoCyclicDependencies();
    }

    private void checkThatAllBindingsExist() {
        Collection<BeanDefinition> beanDefinitions = beansDefinitionStorage.values();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (beanDefinition.getConstructorParametersCount() != 0) {
                Class<?>[] constructorParametersTypes = beanDefinition
                    .getConstructorParametersTypes();
                for (Class<?> constructorParameterType : constructorParametersTypes) {
                    if (!beansDefinitionStorage.containsKey(constructorParameterType)) {
                        throw new BindingNotFoundException(
                            "No building found for the type " + constructorParameterType);
                    }
                }
            }
        }
    }

    private void findCyclicDependencies(Class<?> type, BeanDefinition beanDefinition) {
        Class<?>[] constructorParametersTypes = beanDefinition.getConstructorParametersTypes();
        for (Class<?> constructorParameterType : constructorParametersTypes) {
            if (constructorParameterType.equals(type)) {
                throw new CyclicDependencyException("A cyclic dependence was found for the type:");
            } else {
                BeanDefinition nextBeanDefinition = getBeanDefinitionByType(
                    constructorParameterType);
                findCyclicDependencies(type, nextBeanDefinition);
            }
        }
    }

    private void checkThatThereAreNoCyclicDependencies() {
        beansDefinitionStorage.forEach(this::findCyclicDependencies);
    }

    private Map<Class<?>, BeanDefinition> getAllTheNecessaryDefinitionsForTheType(Class<?> type,
        Map<Class<?>, BeanDefinition> beanDefinitions) {
        if (!hasBinding(type)) {
            throw new BindingNotFoundException("No building found for the type " + type);
        }
        if (!type.isInterface()) {
            type = interfaceByAClass.get(type);
        }
        BeanDefinition beanDefinition = getBeanDefinitionByType(type);
        if (beanDefinitions.containsKey(type)) {
            return beanDefinitions;
        }
        beanDefinitions.put(type, beanDefinition);
        for (Class<?> constructorParameterType : beanDefinition.getConstructorParametersTypes()) {
            getAllTheNecessaryDefinitionsForTheType(constructorParameterType, beanDefinitions);
        }
        return beanDefinitions;
    }

    private void checkTypeOnCyclicDependencies(Class<?> type) {
        Map<Class<?>, BeanDefinition> allTheNecessaryDefinitionsForTheType
            = getAllTheNecessaryDefinitionsForTheType(type, new HashMap<>());
        allTheNecessaryDefinitionsForTheType
            .forEach(this::findCyclicDependencies);
    }
}
