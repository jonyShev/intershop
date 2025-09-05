# v4.0 (реактивная версия с авторизацией пользователей и OAuth2 между сервисами)

## Описание проекта

Мультимодульный реактивный проект на Spring Boot 3:

intershop-core - витрина (товары, карточка товара, корзина, заказы), аутентификация пользователя (логин/пароль) через Spring Security.

payment-service - RESTful-сервис баланса/платежей, защищён как OAuth2 Resource Server.

Redis - кеш товаров (список/карточка).

Keycloak - выдает client_credentials токен для межсервисного вызова shop-app → payment-service.

## Технологии
- Java 21
- Spring Boot 3
- Spring WebFlux
- Spring Data R2DBC
- Spring Data Redis (reactive)
- Spring Security
- OpenAPI (генерация клиентского и серверного кода)
- Maven (мультимодульная сборка)
- Lombok, MapStruct
- Docker, Docker Compose
- JUnit 5, Spring Boot Test, Testcontainers
- OAuth2: Client Credentials (intershop-core — клиент; payment-service — ресурс-сервер)

## Сборка

```bash
./mvnw clean install
```

## Запуск через Docker Compose

```bash
docker-compose up --build
```

После запуска:

Витрина: http://localhost:8080
Платежи: http://localhost:8081
Redis: http://localhost:6379
Keycloak: http://localhost:8083

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
v4.0 — мультимодульная реактивная версия с сервисом платежей и кешированием в Redis с добавлением Spring Security