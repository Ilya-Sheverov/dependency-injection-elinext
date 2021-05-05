package ilya.sheverov.dependencyinjectorelinext.сonstructor.determinant;

import ilya.sheverov.dependencyinjectorelinext.annotation.Inject;
import ilya.sheverov.dependencyinjectorelinext.exception.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

public class ConstructorDeterminantForInjection {
    public Constructor<?> determine(Class<?> aClass) {
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
                        throw new TooManyConstructorsException("There can't be two constructors with the @Inject annotation.");
                    }
                }
                if (withInjectAnnotationConstructor != null) {
                    validateConstructorParameters(withInjectAnnotationConstructor);
                    return withInjectAnnotationConstructor;
                } else if (defaultConstructor != null) {
                    validateConstructorParameters(defaultConstructor);
                    return defaultConstructor;
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
        if (constructor.getParameterCount() == 0) {
            return true;
        }
        return false;
    }

    private void validateConstructorParameters(Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        for (Parameter parameter : parameters) {
            Class<?> type = parameter.getType();
            if (!type.isInterface() || type.isPrimitive()) {
                throw new InvalidConstructorParameterTypeException("The type of the constructor parameter must be an interface.");
            }
        }
    }
}
