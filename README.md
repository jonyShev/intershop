# Intershop - Витрина интернет-магазина (v3.0, реактивная версия с сервисом платежей и кешированием в Redis)

## Описание проекта

Мультимодульный реактивный веб-проект на базе Spring Boot 3, включающий:

Основное приложение "Витрина интернет-магазина" — отображение товаров, корзина, оформление заказов, история покупок.

RESTful-сервис платежей — проверка баланса и списание средств, интеграция по OpenAPI.

Redis — кеширование товаров (список и карточка товара).

Проект реализован на реактивном стеке Spring WebFlux и Spring Data R2DBC.

## Технологии
- Java 21
- Spring Boot 3
- Spring WebFlux
- Spring Data R2DBC
- Spring Data Redis (reactive)
- OpenAPI (генерация клиентского и серверного кода)
- Maven (мультимодульная сборка)
- Lombok, MapStruct
- Docker, Docker Compose
- JUnit 5, Spring Boot Test, Testcontainers

## Как собрать и запустить проект

```bash
./mvnw clean install
./mvnw -pl shop-app spring-boot:run       # Запуск витрины (порт 8080)
./mvnw -pl payment-service spring-boot:run # Запуск платежей (порт 8081)
```

## Запуск через Docker Compose

```bash
docker-compose up --build
```

После запуска:

Витрина: http://localhost:8080
Платежи: http://localhost:8081
Redis: localhost:6379

## Как собрать и запустить через Docker
```bash
docker build -t intershop .
docker run -p 8080:8080 intershop
```

## Тестирование

```bash
./mvnw test
```

## OpenAPI
Файл спецификации: payment-service/src/main/resources/openapi/payment-api.yaml
Генерация:
    - Клиент для shop-app — Maven plugin openapi-generator-maven-plugin
    - Сервер для payment-service — генерация по той же схеме


## Интеграционные тесты (JPA и WebMvc):
Тоже включены в mvn test

## Кеширование в Redis
- Список товаров и карточки кешируются в Redis
- TTL настраивается в application.yaml
- Если данных нет в кеше — загружаются из БД, сохраняются в Redis

## Версия
v3.0 — мультимодульная реактивная версия с сервисом платежей и кешированием в Redis