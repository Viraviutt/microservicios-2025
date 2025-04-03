# Stage 1: Build the JAR file
<<<<<<< HEAD
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
=======
FROM gradle:8.13.0-jdk21 AS build
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle build -x test

# Stage 2: Run the application
FROM amazoncorretto:21-alpine
VOLUME /tmp
COPY --from=build /app/build/libs/*.jar eureka-server.jar
ENTRYPOINT ["java", "-jar", "/eureka-server.jar"]
>>>>>>> 60215f4fae959c176ab098e2649bc867a98d75a0
