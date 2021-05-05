package ilya.sheverov.dependencyinjectorelinext.injector;

import java.lang.reflect.Constructor;

public class BeanDefinition {

    private boolean isSingleton;
    private Class<?> typeOfBean;
    private Constructor constructor;
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
