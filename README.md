# Dependency-injector-elinext

Тестовое задание, которое заключается в написание простейшего Dependency Injection-контейнера.

## Table of contents

* [description](README.md#description)
* [technologies](README.md#technologies)
* [quick build](README.md#Quick-Build)



### Description
[to the table of contents](README.md#table-of-contents)

Используя *Java Reflection API* и классы из пакета *java.lang.reflect* реализовать простейшую версию *Dependency Injection-контейнера*. Результатом работы должен стать *Maven* (или *Gradle*) проект, который может собираться в одну или несколько JAR-библиотек. Сторонние библиотеки использовать запрещено. Весь код должен быть написан вами. За исключением библиотек для тестирования. Для проверки работы библиотеки должны быть написаны Unit-тесты. Основные функциональные требования можно прочитать [здесь](FUNCTIONAL_REQUIREMENTS.md).

### Technologies
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

   После чего у вас появится папка *dependency-injector-elinext*.

2. Далее необходимо собрать **.jar**. Для этого зайдите в директорию где находится pom.xml и выполните команду в консоли:

     `mvn package`

### Style Guide

[to the table of contents](README.md#table-of-contents)

В проекте используется  [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html), за исключением пункта [4.2 Block indentation](https://google.github.io/styleguide/javaguide.html#s4.2-block-indentation). Использует **block indentation**= +4 пробела, вместо 2.

