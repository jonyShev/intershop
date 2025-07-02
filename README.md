# Intershop - Витрина интернет-магазина

## Описание проекта

Spring Boot приложение "Витрина интернет-магазина".  
Позволяет пользователям просматривать товары, добавлять их в корзину и оформлять заказы.

## Технологии

- Java 21
- Spring Boot 3.x
- Spring Web MVC
- Spring Data JPA + Hibernate
- H2 Database (для разработки и тестов)
- Thymeleaf
- Maven
- Docker

## Как собрать проект

```bash
./mvnw clean package
```

# Как запустить локально
```bash
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
mvn test
```

## Интеграционные тесты (JPA и WebMvc):
Тоже включены в mvn test

## Структура проекта
/controller — Контроллеры

/service — Сервисы

/repository — Репозитории

/model — Модели данных

/dto — DTO

/mapper — Мапперы

/resources/templates — HTML-шаблоны