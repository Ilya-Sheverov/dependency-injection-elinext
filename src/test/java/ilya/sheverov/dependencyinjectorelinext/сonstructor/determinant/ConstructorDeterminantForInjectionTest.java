package ilya.sheverov.dependencyinjectorelinext.сonstructor.determinant;

import ilya.sheverov.dependencyinjectorelinext.annotation.Inject;
import ilya.sheverov.dependencyinjectorelinext.exception.ConstructorNotFoundException;
import ilya.sheverov.dependencyinjectorelinext.exception.InvalidConstructorParameterTypeException;
import ilya.sheverov.dependencyinjectorelinext.exception.TooManyConstructorsException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

class ConstructorDeterminantForInjectionTest {

    private ConstructorDeterminantForInjection constructorDeterminantForInjection =
        new ConstructorDeterminantForInjection();

    @Test
    void testDetermineDefaultConstructor() {
        Constructor<?> constructor = constructorDeterminantForInjection.determine(DefaultConstructor.class);

        assertNotNull(constructor);
    }

    @Test
    void testDetermineAnnotatedConstructor() {
        Constructor<?> constructor = constructorDeterminantForInjection.determine(InjectAnnotatedConstructor.class);

        assertNotNull(constructor);
    }

    @Test
    void testTooManyConstructors() {
        assertThrows(TooManyConstructorsException.class,
            () -> constructorDeterminantForInjection.determine(TooManyInjectAnnotations.class));
    }

    @Test
    void testConstructorNotFound() {
        assertThrows(ConstructorNotFoundException.class, () -> constructorDeterminantForInjection.determine(WithoutPublicConstructors.class));
    }

    @Test
    void testPrimitiveTypeConstructorParameter() {
        assertThrows(InvalidConstructorParameterTypeException.class,
            () -> constructorDeterminantForInjection.determine(PrimitiveTypeConstructorParameter.class));
    }

    @Test
    void testConstructorParameterNotInterface() {
        assertThrows(InvalidConstructorParameterTypeException.class,
            () -> constructorDeterminantForInjection.determine(ConstructorParameterNotInterface.class));
    }
}

class DefaultConstructor {

    public DefaultConstructor() {

    }
}

class InjectAnnotatedConstructor {

    @Inject
    public InjectAnnotatedConstructor() {

    }
}

class TooManyInjectAnnotations {

    @Inject
    public TooManyInjectAnnotations() {
    }

    @Inject
    public TooManyInjectAnnotations(Integer a) {
    }

}

class WithoutPublicConstructors {

    WithoutPublicConstructors() {
    }

    @Inject
    WithoutPublicConstructors(Integer integer) {
    }
}

class PrimitiveTypeConstructorParameter {

    private final Comparable s;
    private final int a;

    @Inject
    public PrimitiveTypeConstructorParameter(Comparable s, int a) {
        this.s = s;
        this.a = a;
    }
}

class ConstructorParameterNotInterface {

    private Integer integer;

    @Inject
    public ConstructorParameterNotInterface(Integer integer) {
        this.integer = integer;
    }
}