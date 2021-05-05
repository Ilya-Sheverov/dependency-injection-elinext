package ilya.sheverov.dependencyinjectorelinext.injector;

public interface BeanDefinitionFactory {

    BeanDefinition getBeanDefinition(Class<?> aClass, boolean isSingleton);
}
