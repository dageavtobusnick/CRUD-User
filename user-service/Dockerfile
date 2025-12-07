FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY user-service/pom.xml user-service/
COPY common-dto/pom.xml common-dto/
COPY common-events/pom.xml common-events/
RUN mvn dependency:go-offline -B -pl user-service

COPY user-service/src user-service/src
COPY common-dto/src common-dto/src
COPY common-events/src common-events/src
RUN mvn clean package -DskipTests -pl user-service

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl

COPY --from=build /app/user-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
