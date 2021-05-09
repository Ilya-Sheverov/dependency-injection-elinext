package ilya.sheverov.dependencyinjectorelinext.bean.constructor;

import java.lang.reflect.Constructor;

/**
 * Простой DTO. Используется в классе {@link ConstructorDeterminant} для предоставления информации о
 * конструкторе.
 *
 * @author Ilya Sheverov
 * @see ConstructorDeterminant#getConstructorForInjection(Class)
 * @since 1.2
 */
public class ConstructorInformation {

    private Constructor<?> constructor;
    private Class<?>[] parametersTypes;

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public ConstructorInformation setConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
        return this;
    }

    public Class<?>[] getParametersTypes() {
        return parametersTypes;
    }

    ConstructorInformation setParametersTypes(Class<?>[] parametersTypes) {
        this.parametersTypes = parametersTypes;
        return this;
    }
}
