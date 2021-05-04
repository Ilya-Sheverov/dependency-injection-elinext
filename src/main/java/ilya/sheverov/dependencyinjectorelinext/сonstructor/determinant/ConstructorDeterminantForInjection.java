package ilya.sheverov.dependencyinjectorelinext.сonstructor.determinant;

import ilya.sheverov.dependencyinjectorelinext.annotation.Inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class ConstructorDeterminantForInjection {
    public Constructor<?> determine(Class<?> aClass) {
        if (!aClass.isInterface()) {
            if (!Modifier.isAbstract(aClass.getModifiers())) {
                Constructor<?>[] constructors = aClass.getConstructors();
                if (constructors.length < 1) {
                    throw new RuntimeException("Не найдено ниодного конструктора");
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
                        throw new RuntimeException("Не может быть два конструтора с аннотацией @Inject ");
                    }
                }
                if (withInjectAnnotationConstructor != null) {
                    return withInjectAnnotationConstructor;
                } else if (defaultConstructor != null) {
                    return defaultConstructor;
                } else {
                    throw new RuntimeException("Не найдено ниодного подхожящего конструктор");
                }
            } else {
                throw new RuntimeException("Нельзя передавать абстрактные классы.");
            }
        } else {
            throw new RuntimeException("Нельзя передавать интерфейсы классы.");
        }
    }

    private boolean isDefaultConstructor(Constructor<?> constructor) {
        if (constructor.getParameterCount() == 0) {
            return true;
        }
        return false;
    }
}
