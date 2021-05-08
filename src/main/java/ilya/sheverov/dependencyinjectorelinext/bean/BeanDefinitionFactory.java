package ilya.sheverov.dependencyinjectorelinext.bean;

import ilya.sheverov.dependencyinjectorelinext.bean.constructor.ConstructorDeterminant;
import ilya.sheverov.dependencyinjectorelinext.bean.constructor.ConstructorInformation;
import ilya.sheverov.dependencyinjectorelinext.exception.IllegalArgumentForBindingException;

import java.lang.reflect.Modifier;

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

    ConstructorDeterminant constructorDeterminant =
        new ConstructorDeterminant();

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
                ConstructorInformation constructorInformation = constructorDeterminant.getConstructorForInjection(aClass);
                beanDefinition.setConstructor(constructorInformation.getConstructor());
                beanDefinition.setConstructorParametersTypes(constructorInformation.getParametersTypes());
                return beanDefinition;
            } else {
                throw new IllegalArgumentForBindingException("You can't pass abstract classes.");
            }
        } else {
            throw new IllegalArgumentForBindingException("You can't pass interfaces.");
        }
    }
}
