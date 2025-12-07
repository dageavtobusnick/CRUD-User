FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY notification-service/pom.xml notification-service/
COPY common-events/pom.xml common-events/
RUN mvn dependency:go-offline -B -pl notification-service

COPY notification-service/src notification-service/src
COPY common-events/src common-events/src
RUN mvn clean package -DskipTests -pl notification-service

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl

COPY --from=build /app/notification-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]