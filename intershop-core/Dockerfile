# Используем официальный образ с JDK 21
FROM eclipse-temurin:21-jdk-alpine

# Указываем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем jar-файл внутрь контейнера
COPY ../target/intershop-0.0.1-SNAPSHOT.jar app.jar

# Копируем папку static внутрь контейнера
COPY src/main/resources/static /app/static

# Открываем порт 8080
EXPOSE 8080

# Команда запуска
ENTRYPOINT ["java", "-jar", "app.jar"]
