FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY config-server/pom.xml config-server/
RUN mvn dependency:go-offline -B -pl config-server

COPY config-server/src config-server/src
RUN mvn clean package -DskipTests -pl config-server

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl

COPY --from=build /app/config-server/target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]