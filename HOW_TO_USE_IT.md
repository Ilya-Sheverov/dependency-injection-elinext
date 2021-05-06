## How to use it

Допустим у вас имеются следующие интерфейсы и их реализации:

```java
public interface SomeDAO {
}

public class SomeDAOImpl implements SomeDAO {
}
```



```java
public interface SomeService {
}

public class SomeServiceImpl implements SomeService {

    private SomeDAO someDAO;

    @Inject
    public SomeServiceImpl(SomeDAO someDAO) {

        this.someDAO = someDAO;
    }
}
```



```java
public interface SomeModel {
}

public class SomeModelImpl implements SomeModel {

    private SomeService someService;

    @Inject
    public SomeModelImpl(SomeService someService) {

        this.someService = someService;
    }
}
```

Для осуществления байндинга в конструктор класса имплементации добавляется аннотация @Inject.

Предположим *SomeServiceImpl* и *SomeDAOImpl* должны быть в единственном экземпляре, а *SomeModelImpl* каждый раз создаваться новый.  Тогда реализация этого будет выглядеть так:

```java
InjectorImpl injector = new InjectorImpl();
injector.bind(SomeModel.class,SomeModelImpl.class);
injector.bindSingleton(SomeService.class,SomeServiceImpl.class);
injector.bindSingleton(SomeDAO.class,SomeDAOImpl.class);
injector.checkBindings();

Provider<SomeModel> someModelProvider = injector.getProvider(SomeModel.class);

SomeModel someModel = someModelProvider.getInstance();
```

1. Сначала мы регистрируем наши бины как **prototype** используя метод `bind()` или как **singleton** используя метод `bindSingleton()`.
2. Используйте метод `checkBindings()` , что бы гарантировать, что все биндинги добавлены и среди них нет циркулярных зависимостей.
3. Затем получаем  Provider, по интерфейсу.
4. Вызывая метод провайдера `getInstance()` мы будем получать каждый раз новый объект класса *SomeModelImpl.class,* но содержащий в себе одни и те же объекты классов *SomeServiceImpl.class*, *SomeDAOImpl.class*.

