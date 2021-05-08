package ilya.sheverov.dependencyinjectorelinext.bean;

import java.lang.reflect.Constructor;

/**
 * Объект класса BeanDefinition хранит необходимую информацию для последующего создания бина.
 * Например, нужно ли создавать бин как синглтон, какой его тип, через какой конструктор его создавать и какие у этого
 * конструктора параметры.
 * <p>
 * Объекты этого классы используются {@link BindingBeansContext} для создания бина.
 *
 * @see BindingBeansContext#getBean(Class)
 * @see BeanDefinitionFactory
 */
public class BeanDefinition {

    /**
     * Указывает на то, требуется ли создавать бин как синглтон.
     *
     * @see ilya.sheverov.dependencyinjectorelinext.injector.Injector#bindSingleton(Class, Class)
     */
    private boolean isSingleton;

    /**
     * Указывает на то, для какого класса создавать бин.
     *
     * @see ilya.sheverov.dependencyinjectorelinext.injector.Injector#bindSingleton(Class, Class)
     */
    private Class<?> typeOfBean;

    /**
     * Указывает на то, через какой конструктор создавать бин.
     * <p>
     * Конструктор определяется при помощи
     * {@link ilya.sheverov.dependencyinjectorelinext.сonstructor.determinant.ConstructorDeterminantForInjection }
     *
     * @see ilya.sheverov.dependencyinjectorelinext.сonstructor.determinant.ConstructorDeterminantForInjection#determine(Class)
     */
    private Constructor constructor;

    /**
     * Параметры конструктора, через который создаётся бин.
     */
    private Class<?>[] constructorParametersTypes;

    public int getConstructorParametersCount() {
        return constructorParametersTypes.length;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public void setSingleton(boolean singleton) {
        isSingleton = singleton;
    }

    public Class<?> getTypeOfBean() {
        return typeOfBean;
    }

    public void setTypeOfBean(Class<?> typeOfBean) {
        this.typeOfBean = typeOfBean;
    }

    public Constructor getConstructor() {
        return constructor;
    }

    public void setConstructor(Constructor constructor) {
        this.constructor = constructor;
    }

    public Class<?>[] getConstructorParametersTypes() {
        return constructorParametersTypes;
    }

    public void setConstructorParametersTypes(Class<?>[] constructorParametersTypes) {
        this.constructorParametersTypes = constructorParametersTypes;
    }
}
