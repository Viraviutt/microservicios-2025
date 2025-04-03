# Stage 1: Build the JAR file
FROM gradle:8.13.0-jdk21 AS build
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle build -x test

# Stage 2: Run the application
FROM amazoncorretto:21-alpine
VOLUME /tmp
COPY --from=build /app/build/libs/*.jar service-discovery.jar
ENTRYPOINT ["java", "-jar", "/service-discovery.jar"]