# Dependency-injector-elinext

Тестовое задание, которое заключается в написание простейшего Dependency Injection-контейнера.

## Table of contents

* [description](README.md#description)
* [technologies](README.md#technologies)
* [quick build](README.md#Quick-Build)

## Description
[to the table of contents](README.md#table-of-contents)

Используя *Java Reflection API* и классы из пакета *java.lang.reflect* реализовать простейшую версию *Dependency Injection-контейнера*. Результатом работы должен стать *Maven* (или *Gradle*) проект, который может собираться в одну или несколько JAR-библиотек. Сторонние библиотеки использовать запрещено. Весь код должен быть написан вами. За исключением библиотек для тестирования. Для проверки работы библиотеки должны быть написаны Unit-тесты. Основные функциональные требования можно прочитать [здесь](FUNCTIONAL_REQUIREMENTS.md). 

Как использовать можно посмотреть [здесь](HOW_TO_USE_IT.md). 

## Technologies
[to the table of contents](README.md#table-of-contents)

В проекте используется:

1. Java 11;
4. JUnit 5;
5. Maven
4. Git

## Quick Build

[to the table of contents](README.md#table-of-contents)

##### Для того, что бы развернуть у себя проект у вас должно быть установлено:

1. Java 11 или выше;
4. Maven 3 или выше;
3. Git.

##### Инструкция по установке:

1. Скачайте репозиторий при помощи команды :

   `$ git clone https://github.com/Ilya-Sheverov/dependency-injector-elinext.git`

   После чего у вас появится директория *dependency-injector-elinext*.

2. Далее необходимо собрать **.jar**. Для этого зайдите в директорию где находится pom.xml и выполните команду в консоли:

     `mvn package`

## Style Guide

[to the table of contents](README.md#table-of-contents)

В проекте используется  [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html), за исключением пункта [4.2 Block indentation](https://google.github.io/styleguide/javaguide.html#s4.2-block-indentation). Использует **block indentation**= +4 пробела, вместо 2.

## Assumptions

В данной версии моего контейнера были приняты следующие допущения:

2. Проводится проверка на циклические зависимости между бинами (одному объекту надо передать в конструктор второй объект, а второму требуется передать в конструктор первый) только при вызове метода  `checkBindings()`  у объекта класса  **InjectorImpl.class**. Если не вызвать этот метод и при байдинге будут обнаружены циклические зависимости, будет ошибка StackOverflowError. 
2. Считается, что все типы параметров конструкторов бинов являются интерфейсами, если нет, то выбрасывается исключение.

## TODO

- [ ] Добавить тест для проверки, что мой контейнер корректно будет работать в многопоточной среде.
- [ ] Аргументы конструктора могут быть классами, реализующими добавленный в байдинг интерфейс.
- [ ] В случае, если метод `checkBindings()`не был вызван, предотвратить ошибку StackOverflowError.
- [ ] Написать алгоритм метода `checkBindings()` более эффективно.
- [ ] 
- [ ] Пересмотреть архитектуру, сделать её гибкой и интуитивно понятной.
- [ ] Написать JavaDoc.

