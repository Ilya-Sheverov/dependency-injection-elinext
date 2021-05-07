package ilya.sheverov.dependencyinjectorelinext.bean;

/**
 * Используется для получения {@link BeanDefinition}
 */
public interface BeanDefinitionFactory {

    /**
     * Возвращает {@link BeanDefinition} на основе полученной информации.
     *
     * @param aClass      класс для которого возвращается {@code BeanDefinition}.
     * @param isSingleton требуется ли объект получаемого класса реализовать как сингтлон.
     * @return объект класса {@link BeanDefinition}.
     */
    BeanDefinition getBeanDefinition(Class<?> aClass, boolean isSingleton);

}