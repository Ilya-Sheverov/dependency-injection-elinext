package ilya.sheverov.dependencyinjectorelinext.сonstructor.determinant;

import ilya.sheverov.dependencyinjectorelinext.annotation.Inject;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

class ConstructorDeterminantForInjectionTest {

    ConstructorDeterminantForInjection constructorDeterminantForInjection = new ConstructorDeterminantForInjection();

    @Test
    void determineDefaultConstructor() {
        Constructor<?> constructor = constructorDeterminantForInjection.determine(DefaultConstructorClass.class);

        assertNotNull(constructor);
    }

    @Test
    void determineInjectAnnotatedConstructor() {
        Constructor<?> constructor = constructorDeterminantForInjection.determine(InjectAnnotatedConstructorClass.class);

        assertNotNull(constructor);
    }

    @Test
    void tooManyInjectAnnotations() {
        assertThrows(RuntimeException.class, () -> constructorDeterminantForInjection.determine(TooManyInjectAnnotationsClass.class));
    }

    @Test
    void constructorNotFoundException() {
        assertThrows(RuntimeException.class, () -> constructorDeterminantForInjection.determine(WithoutPublicConstructorsClass.class));
    }

}

class DefaultConstructorClass {

    public DefaultConstructorClass() {

    }
}

class InjectAnnotatedConstructorClass {

    @Inject
    public InjectAnnotatedConstructorClass() {

    }
}

class TooManyInjectAnnotationsClass {

    @Inject
    public TooManyInjectAnnotationsClass() {
    }

    @Inject
    public TooManyInjectAnnotationsClass(Integer a) {
    }

}

class WithoutPublicConstructorsClass {

    WithoutPublicConstructorsClass() {
    }

    @Inject
    WithoutPublicConstructorsClass(Integer integer) {
    }
}
