# Stage 1: Build the JAR file
FROM maven:3.9.7-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
# Stage 2: Run the application
FROM amazoncorretto:21-alpine
VOLUME /tmp
COPY --from=build /app/target/*.jar eureka-server.jar
ENTRYPOINT ["java","-jar","/eureka-server.jar"]