package ilya.sheverov.dependencyinjectorelinext.injector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ilya.sheverov.dependencyinjectorelinext.annotation.Inject;
import ilya.sheverov.dependencyinjectorelinext.exception.BindingNotFoundException;
import ilya.sheverov.dependencyinjectorelinext.exception.CyclicDependencyException;
import ilya.sheverov.dependencyinjectorelinext.exception.IllegalArgumentForBindingException;
import ilya.sheverov.dependencyinjectorelinext.provider.Provider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

interface BeanOne {

    BeanTwo getBeanTwo();

    BeanThree getBeanThree();

}

interface BeanTwo {

    BeanThree getBeanThree();

}

interface BeanThree {

}

interface BeanFour {

    BeanOneImpl getBeanOne();

    BeanTwoImpl getBeanTwo();

    BeanThreeImpl getBeanThree();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BeanOneImpl)) {
            return false;
        }
        BeanOneImpl beanOne = (BeanOneImpl) o;
        return beanTwo.equals(beanOne.beanTwo) &&
            beanThree.equals(beanOne.beanThree);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanTwo, beanThree);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BeanOneAbstract)) {
            return false;
        }
        BeanOneAbstract that = (BeanOneAbstract) o;
        return beanTwo.equals(that.beanTwo) &&
            beanThree.equals(that.beanThree);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanTwo, beanThree);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BeanTwoImpl)) {
            return false;
        }
        BeanTwoImpl beanTwo = (BeanTwoImpl) o;
        return Objects.equals(beanThree, beanTwo.beanThree);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanThree);
    }
}

class BeanThreeImpl implements BeanThree {

    public BeanThreeImpl() {
    }
}

class BeanFourImpl implements BeanFour {

    private final BeanOneImpl beanOne;
    private final BeanTwoImpl beanTwo;
    private final BeanThreeImpl beanThree;

    @Inject
    public BeanFourImpl(BeanOneImpl beanOne, BeanTwoImpl beanTwo, BeanThreeImpl beanThree) {
        this.beanOne = beanOne;
        this.beanTwo = beanTwo;
        this.beanThree = beanThree;
    }

    @Override
    public BeanOneImpl getBeanOne() {
        return beanOne;
    }

    @Override
    public BeanTwoImpl getBeanTwo() {
        return beanTwo;
    }

    @Override
    public BeanThreeImpl getBeanThree() {
        return beanThree;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BeanFourImpl)) {
            return false;
        }
        BeanFourImpl beanFour = (BeanFourImpl) o;
        return beanOne.equals(beanFour.beanOne) &&
            beanTwo.equals(beanFour.beanTwo) &&
            beanThree.equals(beanFour.beanThree);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanOne, beanTwo, beanThree);
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
    void testConstructorParametersAreNotInterfaces() {
        Injector injector = new InjectorImpl();
        injector.bindSingleton(BeanOne.class, BeanOneImpl.class);
        injector.bindSingleton(BeanTwo.class, BeanTwoImpl.class);
        injector.bind(BeanThree.class, BeanThreeImpl.class);
        injector.bind(BeanFour.class, BeanFourImpl.class);

        Provider<BeanFour> beanFourProvider = injector.getProvider(BeanFour.class);

        BeanFour beanFour = beanFourProvider.getInstance();

        assertNotNull(beanFour);
        assertNotNull(beanFour.getBeanOne());
        assertNotNull(beanFour.getBeanTwo());
        assertNotNull(beanFour.getBeanThree());
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
    void testCyclicBeansWithoutCheckingMethod() {
        InjectorImpl injector = new InjectorImpl();
        injector.bindSingleton(CyclicBeanOne.class, CyclicBeanOneImpl.class);
        injector.bindSingleton(CyclicBeanTwo.class, CyclicBeanTwoImpl.class);
        assertThrows(CyclicDependencyException.class,
            () -> injector.getProvider(CyclicBeanOne.class)
        );


    }

    @Test
    void testCheckThatNotAllBindingsExist() {
        InjectorImpl injector = new InjectorImpl();
        injector.bind(BeanTwo.class, BeanTwoImpl.class);

        assertThrows(BindingNotFoundException.class,
            injector::checkBindings);
    }


    /**
     * В тесте проверяется, что объект, который должен создаться один раз, не создаться больше более
     * одного. Для этого, каждый поток сохраняет в синхронизированный {@code Set<Object> objects}
     * полученный инстанс. Если дупликата не произошло, то размер objects будет равен 1.
     */
    @Test
    void testConcurrent() throws InterruptedException {
        InjectorImpl injector = new InjectorImpl();
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


