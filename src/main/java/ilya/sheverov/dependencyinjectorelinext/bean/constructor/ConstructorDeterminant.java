package ilya.sheverov.dependencyinjectorelinext.bean.constructor;

import ilya.sheverov.dependencyinjectorelinext.annotation.Inject;
import ilya.sheverov.dependencyinjectorelinext.exception.ConstructorNotFoundException;
import ilya.sheverov.dependencyinjectorelinext.exception.IllegalArgumentForBindingException;
import ilya.sheverov.dependencyinjectorelinext.exception.InvalidConstructorParameterTypeException;
import ilya.sheverov.dependencyinjectorelinext.exception.TooManyConstructorsException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

/**
 * Класс содержит метод, который позволяется определить конструктор, через который DI-контейнером
 * будет создаваться бин.
 * <p>
 * Проверяет, что аннотация {@link Inject} присутствует в единственном экземпляре или отсутствует
 * вовсе. Тогда будет выбран конструктор по умолчанию. Если будет обнаружено больше одной аннотации
 * {@link Inject}, то будет выброшено исключение {@link TooManyConstructorsException}. Если не будет
 * найдено ни одного подходящего конструктора, то будет выброшено исключение {@link
 * ConstructorNotFoundException}.
 * <p>
 * Также проверяются параметры конструктора. Параметры конструктор не должны быть примитивного типа.
 * Если пареметры конструктора не соответствуют требования, то будет выброшено исключение {@link
 * InvalidConstructorParameterTypeException}.
 *
 * @author Ilya Sheverov
 * @see ilya.sheverov.dependencyinjectorelinext.bean.BeanDefinitionFactory#getBeanDefinition(Class,
 * boolean)
 * @since 1.0
 */
public class ConstructorDeterminant {

    public ConstructorInformation getConstructorForInjection(Class<?> aClass) {
        if (!aClass.isInterface()) {
            if (!Modifier.isAbstract(aClass.getModifiers())) {
                Constructor<?>[] constructors = aClass.getConstructors();
                if (constructors.length < 1) {
                    throw new ConstructorNotFoundException("No constructor found.");
                }
                int constructorWithInjectAnnotationCount = 0;
                Constructor<?> defaultConstructor = null;
                Constructor<?> withInjectAnnotationConstructor = null;
                for (Constructor<?> constructor : constructors) {
                    Inject annotation = constructor.getAnnotation(Inject.class);
                    if (annotation != null) {
                        withInjectAnnotationConstructor = constructor;
                        constructorWithInjectAnnotationCount++;
                    } else {
                        if (isDefaultConstructor(constructor)) {
                            defaultConstructor = constructor;
                        }
                    }
                    if (constructorWithInjectAnnotationCount > 1) {
                        throw new TooManyConstructorsException(
                            "There can't be two constructors with the @Inject annotation.");
                    }
                }
                if (withInjectAnnotationConstructor != null) {
                    validateConstructorParameters(withInjectAnnotationConstructor);
                    Class<?>[] constructorParametersTypes = getConstructorParametersTypes(
                        withInjectAnnotationConstructor);
                    ConstructorInformation constructorInformation = new ConstructorInformation();
                    constructorInformation
                        .setConstructor(withInjectAnnotationConstructor)
                        .setParametersTypes(constructorParametersTypes);
                    return constructorInformation;
                } else if (defaultConstructor != null) {
                    validateConstructorParameters(defaultConstructor);
                    ConstructorInformation constructorInformation = new ConstructorInformation();
                    constructorInformation
                        .setConstructor(defaultConstructor)
                        .setParametersTypes(new Class[0]);
                    return constructorInformation;
                } else {
                    throw new ConstructorNotFoundException("No matching constructor found.");
                }
            } else {
                throw new IllegalArgumentForBindingException("You can't pass abstract classes.");
            }
        } else {
            throw new IllegalArgumentForBindingException("You can't pass non-interfaces.");
        }
    }

    private boolean isDefaultConstructor(Constructor<?> constructor) {
        return constructor.getParameterCount() == 0;
    }

    private Class<?>[] getConstructorParametersTypes(Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        Class<?>[] parametersTypes = new Class[parameters.length];
        int parameterNumber = 0;
        for (Parameter parameter : parameters) {
            parametersTypes[parameterNumber] = parameter.getType();
            parameterNumber++;
        }
        return parametersTypes;
    }

    private void validateConstructorParameters(Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        for (Parameter parameter : parameters) {
            Class<?> type = parameter.getType();
            if (type.isPrimitive()) {
                throw new InvalidConstructorParameterTypeException(
                    "The type of the constructor parameter must be an interface.");
            }
        }
    }
}
