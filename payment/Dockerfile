# Etapa 1: Construcción de la aplicación con Gradle
FROM gradle:8.13.0-jdk21 AS build
WORKDIR /app

# Copiar los archivos de configuración primero para aprovechar la caché de Docker
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Deshabilitar el daemon de Gradle y ejecutar la descarga de dependencias
ENV GRADLE_USER_HOME="/home/gradle/.gradle"
RUN gradle --no-daemon dependencies

# Copiar el resto del código fuente
COPY . .

# Construcción sin daemon para evitar bloqueos
RUN gradle --no-daemon build -x test

# Etapa 2: Imagen final con JDK ligero para ejecutar la aplicación
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
COPY --from=build /app .
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
