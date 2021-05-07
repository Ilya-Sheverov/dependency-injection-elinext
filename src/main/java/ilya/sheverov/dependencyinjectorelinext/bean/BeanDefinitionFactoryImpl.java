package ilya.sheverov.dependencyinjectorelinext.bean;

import ilya.sheverov.dependencyinjectorelinext.exception.IllegalArgumentForBindingException;
import ilya.sheverov.dependencyinjectorelinext.сonstructor.determinant.ConstructorDeterminantForInjection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

/**
 * Позволяет получать {@link BeanDefinition}.
 * <p>
 * Используется в классе {@link BindingBeansContext}.
 *
 * @see BindingBeansContext#bind(Class, Class, boolean).
 */
public class BeanDefinitionFactoryImpl implements BeanDefinitionFactory {

    ConstructorDeterminantForInjection constructorDeterminantForInjection =
        new ConstructorDeterminantForInjection();

    /**
     * Возвращает {@link BeanDefinition} на основе полученной информации.
     *
     * @param aClass      класс для которого возвращается {@code BeanDefinition}.
     * @param isSingleton требуется ли объект получаемого класса реализовать как сингтлон.
     * @return объект класса {@link BeanDefinition}.
     */
    @Override
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
