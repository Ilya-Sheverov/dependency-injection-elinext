package ilya.sheverov.dependencyinjectorelinext.injector;

import ilya.sheverov.dependencyinjectorelinext.annotation.Inject;
import ilya.sheverov.dependencyinjectorelinext.provider.Provider;
import ilya.sheverov.dependencyinjectorelinext.сonstructor.determinant.ConstructorDeterminantForInjection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

public class InjectorImpl implements Injector {
    ConstructorDeterminantForInjection cDFI = new ConstructorDeterminantForInjection();

    Map<Class<?>, BeanDefinition> beansDefinitionContext = new HashMap<>();

    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        if (type.isInterface()) {
            BeanDefinition beanDefinition = beansDefinitionContext.get(type);
            if (beanDefinition != null) {
                if (beanDefinition.constructorArgCount == 0) {
                    try {
                        Object bean = beanDefinition.getConstructor().newInstance();
                        Provider<T> provider = new Provider<>() {
                            @Override
                            public T getInstance() {
                                return (T) bean;
                            }
                        };
                        return provider;
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
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
                if (beansDefinitionContext.get(intf) == null) {
                    Constructor<?> constructor = cDFI.determine(impl);
                    BeanDefinition beanDefinition = new BeanDefinition();
                    beanDefinition.setTypeForCreatingBean(impl);
                    beanDefinition.setConstructor(constructor);
                    beansDefinitionContext.put(intf, beanDefinition);
                } else {
                    throw new RuntimeException("Нельзя использовать больше одной реализации " + intf.getClasses());
                }
            }
        }

    }

    @Override
    public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {

    }

    class BeanDefinition {
        Constructor constructor;
        Class<?> typeForCreatingBean;
        int constructorArgCount;
        List<Class<?>> typesConstArgs = new ArrayList<>();
        boolean isSingleton;

        public Constructor getConstructor() {
            return constructor;
        }

        public void setConstructor(Constructor constructor) {
            this.constructor = constructor;
        }

        public Class<?> getTypeForCreatingBean() {
            return typeForCreatingBean;
        }

        public void setTypeForCreatingBean(Class<?> typeForCreatingBean) {
            this.typeForCreatingBean = typeForCreatingBean;
        }

        public int getConstructorArgCount() {
            return constructorArgCount;
        }

        public void setConstructorArgCount(int constructorArgCount) {
            this.constructorArgCount = constructorArgCount;
        }

        public List<Class<?>> getTypesConstArgs() {
            return typesConstArgs;
        }

        public void setTypesConstArgs(List<Class<?>> typesConstArgs) {
            this.typesConstArgs = typesConstArgs;
        }

        public boolean isSingleton() {
            return isSingleton;
        }

        public void setSingleton(boolean singleton) {
            isSingleton = singleton;
        }
    }
}
