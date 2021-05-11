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

Для осуществления байндинга в конструктор класса имплементации добавляется аннотация @Inject. При отсутствии конструкторов с аннотацией Inject используется конструктор по умолчанию. 

Предположим, объекты  классов *SomeServiceImpl* и *SomeDAOImpl* должны создаваться в единственном экземпляре,
а объект класса *SomeModelImpl* должен каждый раз создаваться новый.
Реализация такого поведения будет выглядеть так:

```java
public class Main {
    public static void main(String[] args) {
        InjectorImpl injector = new InjectorImpl();
        injector.bind(SomeModel.class, SomeModelImpl.class);
        injector.bindSingleton(SomeService.class, SomeServiceImpl.class);
        injector.bindSingleton(SomeDAO.class, SomeDAOImpl.class);
        injector.checkBindings();
        
        Provider<SomeModel> someModelProvider = injector.getProvider(SomeModel.class);

        SomeModel someModel = someModelProvider.getInstance();
    }
}
```

1. Сначала мы регистрируем наши бины как **prototype** используя метод `bind()` или как **singleton** используя метод `bindSingleton()`.
2. Используем метод `checkBindings()`, что бы гарантировать, что все необходимые биндинги добавлены и среди них нет циклических зависимостей.
Если не использовать данный метод, то проверка будет происходить при создании бина, что приведет к некоторым временным издержкам. 
3. Затем получаем Provider, передавая интерфейс в качестве параметра методу `getProvider()`.
4. Вызывая метод провайдера `getInstance()` мы получаем каждый раз новый объект
класса *SomeModelImpl.class* (так как он был зарегистрирован как prototype),
но содержащий в себе одни и те же объекты классов *SomeServiceImpl.class*, *SomeDAOImpl.class*(так как они были зарегистрированы как singleton).

