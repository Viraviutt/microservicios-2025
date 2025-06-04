FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY .mvn ./.mvn
COPY mvnw .
COPY mvnw.cmd .
RUN apk add --no-cache maven

COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]