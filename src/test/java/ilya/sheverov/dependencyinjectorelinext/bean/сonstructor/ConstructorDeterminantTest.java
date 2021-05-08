package ilya.sheverov.dependencyinjectorelinext.bean.сonstructor;

import ilya.sheverov.dependencyinjectorelinext.annotation.Inject;
import ilya.sheverov.dependencyinjectorelinext.bean.constructor.ConstructorDeterminant;
import ilya.sheverov.dependencyinjectorelinext.bean.constructor.ConstructorInformation;
import ilya.sheverov.dependencyinjectorelinext.exception.ConstructorNotFoundException;
import ilya.sheverov.dependencyinjectorelinext.exception.InvalidConstructorParameterTypeException;
import ilya.sheverov.dependencyinjectorelinext.exception.TooManyConstructorsException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

class ConstructorDeterminantTest {

    private ConstructorDeterminant constructorDeterminant =
        new ConstructorDeterminant();

    @Test
    void testDetermineDefaultConstructor() {
        ConstructorInformation constructorInformation = constructorDeterminant.getConstructorForInjection(DefaultConstructor.class);

        assertNotNull(constructorInformation);
        assertNotNull(constructorInformation.getConstructor());
        assertNotNull(constructorInformation.getParametersTypes());
    }

    @Test
    void testDetermineAnnotatedConstructor() {
        ConstructorInformation constructorInformation = constructorDeterminant.getConstructorForInjection(InjectAnnotatedConstructor.class);

        assertNotNull(constructorInformation);
        assertNotNull(constructorInformation.getConstructor());
        assertNotNull(constructorInformation.getParametersTypes());
    }

    @Test
    void testTooManyConstructors() {
        assertThrows(TooManyConstructorsException.class,
            () -> constructorDeterminant.getConstructorForInjection(TooManyInjectAnnotations.class));
    }

    @Test
    void testConstructorNotFound() {
        assertThrows(ConstructorNotFoundException.class, () -> constructorDeterminant.getConstructorForInjection(WithoutPublicConstructors.class));
    }

    @Test
    void testPrimitiveTypeConstructorParameter() {
        assertThrows(InvalidConstructorParameterTypeException.class,
            () -> constructorDeterminant.getConstructorForInjection(PrimitiveTypeConstructorParameter.class));
    }

}
