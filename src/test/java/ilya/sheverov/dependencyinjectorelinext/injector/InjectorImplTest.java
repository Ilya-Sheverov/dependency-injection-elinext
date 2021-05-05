package ilya.sheverov.dependencyinjectorelinext.injector;

import ilya.sheverov.dependencyinjectorelinext.annotation.Inject;
import ilya.sheverov.dependencyinjectorelinext.provider.Provider;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.*;

interface DefaultConstructorInterface {
}

interface EventDAO {
}

interface EventService {

}

class DefaultConstructorClass implements DefaultConstructorInterface {

    public DefaultConstructorClass() {

    }
}

class InMemoryEventDAOImpl implements EventDAO {
    public InMemoryEventDAOImpl() {
    }
}

class EventServiceImpl implements EventService {

    private EventDAO eventDAO;

    public EventServiceImpl() {

    }

    @Inject
    public EventServiceImpl(EventDAO eventDAO) {
        this.eventDAO = eventDAO;
    }
}

class InjectorImplTest {

    @Test
    void getProvider() {
        Injector injector = new InjectorImpl();
        injector.bind(DefaultConstructorInterface.class, DefaultConstructorClass.class);


    }

    @Test
    void testEventServiceImplBinding() {
        Injector injector = new InjectorImpl();
        injector.bind(EventDAO.class, InMemoryEventDAOImpl.class);
        injector.bind(EventService.class, EventServiceImpl.class);

        Provider<EventService> provider = injector.getProvider(EventService.class);

        assertNotNull(provider);
        assertNotNull(provider.getInstance());
        assertSame(EventServiceImpl.class, provider.getInstance().getClass());

    }

    @Test
    void bind() {
    }

    @Test
    void bindSingleton() {
    }
}

