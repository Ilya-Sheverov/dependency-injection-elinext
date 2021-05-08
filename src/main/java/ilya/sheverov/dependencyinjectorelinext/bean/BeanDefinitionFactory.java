package ilya.sheverov.dependencyinjectorelinext.bean;

import ilya.sheverov.dependencyinjectorelinext.exception.IllegalArgumentForBindingException;
import ilya.sheverov.dependencyinjectorelinext.сonstructor.determinant.ConstructorDeterminantForInjection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

/**
 * Фабрика предназначена для получения объектов класса {@link BeanDefinition} на основе информации, переданной в метод
 * {@code getBeanDefinition(Class<?> aClass, boolean isSingleton)}.
 * <p>
 * Используется в классе {@link ilya.sheverov.dependencyinjectorelinext.injector.InjectorImpl}.
 *
 * @author Ilya Sheverov
 * @see #getBeanDefinition(Class, boolean)
 * @see ilya.sheverov.dependencyinjectorelinext.injector.InjectorImpl#bind(Class, Class, boolean)
 * @since 1.0
 */
public class BeanDefinitionFactory {

    ConstructorDeterminantForInjection constructorDeterminantForInjection =
        new ConstructorDeterminantForInjection();

    /**
     * Возвращает {@link BeanDefinition} на основе переданной в метод информации.
     *
     * @param aClass      класс, на информации которого создается объект {@link BeanDefinition}.
     * @param isSingleton является ли бин синглтоном.
     * @return объект класса {@link BeanDefinition}.
     */
    public BeanDefinition getBeanDefinition(Class<?> aClass, boolean isSingleton) {
        if (!aClass.isInterface()) {
            if (!Modifier.isAbstract(aClass.getModifiers())) {
                BeanDefinition beanDefinition = new BeanDefinition();
                beanDefinition.setTypeOfBean(aClass);
                beanDefinition.setSingleton(isSingleton);
                Constructor<?> constructor = constructorDeterminantForInjection.determine(aClass);
                beanDefinition.setConstructor(constructor);
                Class<?>[] parametersTypes = getConstructorParametersTypes(constructor);
                beanDefinition.setConstructorParametersTypes(parametersTypes);
                return beanDefinition;
            } else {
                throw new IllegalArgumentForBindingException("You can't pass abstract classes.");
            }
        } else {
            throw new IllegalArgumentForBindingException("You can't pass interfaces.");
        }
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
}
