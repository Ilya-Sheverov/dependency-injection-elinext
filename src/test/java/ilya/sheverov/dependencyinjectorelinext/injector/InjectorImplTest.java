package ilya.sheverov.dependencyinjectorelinext.injector;

import ilya.sheverov.dependencyinjectorelinext.annotation.Inject;
import ilya.sheverov.dependencyinjectorelinext.exception.BindingNotFoundException;
import ilya.sheverov.dependencyinjectorelinext.exception.CyclicDependencyException;
import ilya.sheverov.dependencyinjectorelinext.exception.IllegalArgumentForBindingException;
import ilya.sheverov.dependencyinjectorelinext.exception.InvalidConstructorParameterTypeException;
import ilya.sheverov.dependencyinjectorelinext.provider.Provider;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class InjectorImplTest {

    @Test
    void testBinding() {
        Injector injector = new InjectorImpl();
        injector.bind(BeanOne.class, BeanOneImpl.class);
        injector.bind(BeanTwo.class, BeanTwoImpl.class);
        injector.bindSingleton(BeanThree.class, BeanThreeImpl.class);

        Provider<BeanOne> beanOneImplProvider = injector.getProvider(BeanOne.class);

        assertNotNull(beanOneImplProvider);

        BeanOne beanOneImpl = beanOneImplProvider.getInstance();
        assertNotNull(beanOneImpl);
        assertEquals(beanOneImpl.getBeanThree(), beanOneImpl.getBeanTwo().getBeanThree());
    }

    @Test
    void testSingletonsOnly() {
        Injector injector = new InjectorImpl();
        injector.bindSingleton(BeanOne.class, BeanOneImpl.class);
        injector.bindSingleton(BeanTwo.class, BeanTwoImpl.class);
        injector.bindSingleton(BeanThree.class, BeanThreeImpl.class);

        Provider<BeanOne> beanOneImplProvider = injector.getProvider(BeanOne.class);

        assertNotNull(beanOneImplProvider);

        BeanOne beanOneImpl = beanOneImplProvider.getInstance();
        assertNotNull(beanOneImpl);
        assertEquals(beanOneImpl.getBeanThree(), beanOneImpl.getBeanTwo().getBeanThree());
    }

    @Test
    void testBindingNotFound() {
        Injector injector = new InjectorImpl();

        Provider<BeanOne> beanOneProvider = injector.getProvider(BeanOne.class);

        assertNull(beanOneProvider);
    }

    @Test
    void testBindingForConstructorArgumentNotFound() {
        Injector injector = new InjectorImpl();
        injector.bind(BeanTwo.class, BeanTwoImpl.class);

        assertThrows(BindingNotFoundException.class,
            () -> injector.getProvider(BeanTwo.class).getInstance());
    }

    @Test
    void testFirstParameterIsNotAnInterfaceForBinding() {
        Injector injector = new InjectorImpl();

        assertThrows(IllegalArgumentForBindingException.class,
            () -> injector.bindSingleton(BeanOneImpl.class, BeanOneImpl.class));
    }

    @Test
    void testSecondParameterCannotBeAbstractClassForBinding() {
        Injector injector = new InjectorImpl();

        assertThrows(IllegalArgumentForBindingException.class,
            () -> injector.bindSingleton(BeanOne.class, BeanOneAbstract.class));
    }

    @Test
    void testCyclicBeans() {
        InjectorImpl injector = new InjectorImpl();
        injector.bindSingleton(CyclicBeanOne.class, CyclicBeanOneImpl.class);
        injector.bindSingleton(CyclicBeanTwo.class, CyclicBeanTwoImpl.class);

        assertThrows(CyclicDependencyException.class,
            injector::checkBindings);

    }

    @Test
    void testCheckThatAllBindingsExist() {
        InjectorImpl injector = new InjectorImpl();
        injector.bind(BeanTwo.class, BeanTwoImpl.class);

        assertThrows(BindingNotFoundException.class,
            injector::checkBindings);
    }


    /**
     * В тесте проверяется, что объект, который должен создаться один раз, не создаться больше более одного.
     * Для этого, каждый поток сохраняет в синхронизированный {@code Set<Object> objects} полученный инстанс. Если
     * дупликата не произошло, то размер objects будет равен 1.
     */
    @Test
    void testConcurrent() throws InterruptedException {
        Injector injector = new InjectorImpl();
        injector.bindSingleton(BeanOne.class, BeanOneImpl.class);
        injector.bindSingleton(BeanTwo.class, BeanTwoImpl.class);
        injector.bind(BeanThree.class, BeanThreeImpl.class);

        Provider<BeanOne> beanOneImplProvider = injector.getProvider(BeanOne.class);
        CountDownLatch count = new CountDownLatch(2021);
        Set<Object> objects = Collections.synchronizedSet(new HashSet<>());
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 2021; i++) {
            executorService.execute(() -> {
                try {
                    Thread.yield();
                    TimeUnit.MILLISECONDS.sleep(300);
                    Thread.yield();
                    BeanOne beanOne = beanOneImplProvider.getInstance();
                    objects.add(beanOne);
                    count.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        count.await();

        assertEquals(1, objects.size());
    }
}

interface BeanOne {

    BeanTwo getBeanTwo();

    BeanThree getBeanThree();

}

interface BeanTwo {

    BeanThree getBeanThree();

}

interface BeanThree {
}

interface CyclicBeanOne {
}

interface CyclicBeanTwo {
}

class BeanOneImpl implements BeanOne {

    private final BeanTwo beanTwo;
    private final BeanThree beanThree;

    @Inject
    public BeanOneImpl(BeanTwo beanTwo, BeanThree beanThree) {
        this.beanTwo = beanTwo;
        this.beanThree = beanThree;
    }

    public BeanTwo getBeanTwo() {
        return beanTwo;
    }

    public BeanThree getBeanThree() {
        return beanThree;
    }
}

abstract class BeanOneAbstract implements BeanOne {

    private final BeanTwo beanTwo;
    private final BeanThree beanThree;

    @Inject
    public BeanOneAbstract(BeanTwo beanTwo, BeanThree beanThree) {
        this.beanTwo = beanTwo;
        this.beanThree = beanThree;
    }
}

class BeanTwoImpl implements BeanTwo {

    private BeanThree beanThree;

    @Inject
    public BeanTwoImpl(BeanThree beanThree) {
        this.beanThree = beanThree;
    }

    public BeanThree getBeanThree() {
        return beanThree;
    }
}

class BeanThreeImpl implements BeanThree {
    public BeanThreeImpl() {
    }
}

class CyclicBeanOneImpl implements CyclicBeanOne {

    private CyclicBeanTwo cyclicBeanTwo;

    @Inject
    public CyclicBeanOneImpl(CyclicBeanTwo cyclicBeanTwo) {
        this.cyclicBeanTwo = cyclicBeanTwo;
    }
}

class CyclicBeanTwoImpl implements CyclicBeanTwo {

    private CyclicBeanOne cyclicBeanOne;

    @Inject
    public CyclicBeanTwoImpl(CyclicBeanOne cyclicBeanOne) {
        this.cyclicBeanOne = cyclicBeanOne;
    }

}


