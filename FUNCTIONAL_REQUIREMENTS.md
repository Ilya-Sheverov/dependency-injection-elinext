### Functional requirements

##### 1. Запрещается использовать сторонние библиотеки (кроме юнит тестирования)

##### 2. Реализацию необходимо осуществить в классе InjectorImpl на основе интерфейса Injector:

```java
public interface Injector {

  <T> Provider<T> getProvider(Class<T> type); //получение инстанса класса со всеми иньекциями по классу интерфейса

  <T> void bind(Class<T> intf, Class<? extends T> impl); //регистрация байндинга по классу интерфейса и его реализации

  <T> void bindSingleton(Class<T> intf, Class<? extends T> impl); //регистрация синглтон класса 

}
```

Где

```java
public interface Provider<T>{  

   T getInstance();  

}
```

##### 3. Для осуществления байндинга в конструктор класса имплементации добавляется аннотация @Inject

Например:

```java
pubic class  EventServiceImpl implements EventService {

   private EventDao eventDao;       

 	@Inject
	public EventServiceImpl(EventDao eventDao) {
		this.eventDao = eventDao;
	}

  }
```

##### 4. Если в классе присутствует несколько конструкторов с аннотацией @Inject, выбрасывается TooManyConstructorsException.

##### 5. При отсутствии конструкторов с аннотацией Inject используется конструктор по умолчанию. При отсутствии такового выбрасывается ConstructorNotFoundException.

##### 6. Если контейнер использует конструктор с аннотацией Inject и для какого-либо аргумента контейнер не может найти binding, выбрасывается BindingNotFoundException.

##### 7. Если мы запрашиваем Provider для какого-либо класса и нет соответствующего binding, возвращается null.

##### 8. Реализуйте возможность использования Singleton и Prototype бинов.

##### 9. Реализация singleton binding'ов должна быть ленивой (создание объекта при первом обращении).

##### 10. Реализация получения провайдеров должна быть потокобезопасной.

##### 11. Поддержка field и method injection не требуется - Inject только через конструкторы.

##### 12. Все аргументы конструкторов гарантировано являются reference type'ами. То есть не предполагается передача в конструкторы аргументов простых типов.

##### 13. Все конструкторы являются public.



