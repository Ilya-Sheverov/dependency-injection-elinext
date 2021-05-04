package ilya.sheverov.dependencyinjectorelinext.injector;

import ilya.sheverov.dependencyinjectorelinext.annotation.Inject;
import ilya.sheverov.dependencyinjectorelinext.provider.Provider;
import ilya.sheverov.dependencyinjectorelinext.сonstructor.determinant.ConstructorDeterminantForInjection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;

public class InjectorImpl implements Injector {
    ConstructorDeterminantForInjection constructorDeterminantForInjection = new ConstructorDeterminantForInjection();

    Set<Class<?>> beansContext = new HashSet<>();

    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        return null;
    }


    private boolean checkRule(Class<?> impl) {
        if (!Modifier.isAbstract(impl.getModifiers())) {
            Constructor<?>[] constructors = impl.getConstructors();
            if (constructors.length < 1) {
                throw new RuntimeException("Не найдено ниодного конструктор");
            } else {
                int constrWithInjectAnnotationCount = 0;
                Constructor<?> defaultConstructor = null;
                Constructor<?> injectConstructor = null;
                for (Constructor<?> constructor : constructors) {
                    Inject annotation = constructor.getAnnotation(Inject.class);
                    if (annotation != null) {
                        if (constructor.getParameterCount() == 0) {
                            throw new RuntimeException("Нечего инжектить.");
                        }
                        injectConstructor = constructor;
                        constrWithInjectAnnotationCount++;
                    } else {
                        if (constructor.getParameterCount() == 0) {
                            defaultConstructor = constructor;
                        }
                    }
                    if (constrWithInjectAnnotationCount > 1) {
                        throw new RuntimeException("Не может быть два конструтора с аннотацией @Inject ");
                    }
                }
                if (defaultConstructor == null && injectConstructor == null) {
                    throw new RuntimeException("Не найдено ниодного подходящего конструтора");
                }
            }
            return true;
        } else {
            throw new RuntimeException("Нельзя передавать абстрактные классы.");
        }
    }

    @Override
    public <T> void bind(Class<T> intf, Class<? extends T> impl) {
        if (intf.isInterface()) {
            if (checkRule(impl)) {
                if (!beansContext.contains(intf)) {
                    beansContext.add(intf);
                } else {
                    throw new RuntimeException("Нельзя использовать больше одной реализации " + intf.getClasses());
                }
            }
        }

    }

    class BeanDefinition {
        Class<?> typeForCreatingBean;
        int constructorArgCount;
        List<Class<?>> typesConstArgs = new ArrayList<>();
        boolean isSingleton;

    }

    @Override
    public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {

    }
}
