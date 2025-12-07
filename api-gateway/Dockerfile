FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/
RUN mvn dependency:go-offline -B -pl api-gateway

COPY api-gateway/src api-gateway/src
RUN mvn clean package -DskipTests -pl api-gateway

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl

COPY --from=build /app/api-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]