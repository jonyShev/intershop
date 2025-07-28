# Intershop - Витрина интернет-магазина (реактивная версия)

## Описание проекта

Реактивное веб-приложение на базе Spring Boot 3, использующее стек WebFlux и Spring Data R2DBC. 
Проект представляет собой витрину интернет-магазина с возможностью добавления товаров в корзину, оформления заказов и просмотра истории покупок.

## Технологии

- Java 21
- Spring Boot 3
- Spring WebFlux
- Spring Data R2DBC
- R2DBC H2
- Maven
- Lombok + MapStruct
- Docker

## Как собрать и запустить проект

```bash
./mvnw clean install
./mvnw spring-boot:run
```

## Сборка JAR

```bash
./mvnw clean package
java -jar target/intershop-0.0.1-SNAPSHOT.jar
```

После запуска:
Приложение доступно по адресу:
http://localhost:8080

## Как собрать и запустить через Docker
```bash
docker build -t intershop .
docker run -p 8080:8080 intershop
```

## Тестирование
Юнит-тесты (сервисы):
```bash
./mvnw test
```

## Интеграционные тесты (JPA и WebMvc):
Тоже включены в mvn test

## Структура проекта
/controller — Контроллеры

/service — Сервисы

/repository — Репозитории

/model — Модели данных

/dto — DTO

/resources/templates — HTML-шаблоны

## v2.0 — текущая реактивная версия (Spring WebFlux + R2DBC)