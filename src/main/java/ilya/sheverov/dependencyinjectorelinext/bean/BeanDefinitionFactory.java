package ilya.sheverov.dependencyinjectorelinext.bean;

public interface BeanDefinitionFactory {

    BeanDefinition getBeanDefinition(Class<?> aClass, boolean isSingleton);

}
