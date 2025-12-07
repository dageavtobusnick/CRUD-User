dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY discovery-server/pom.xml discovery-server/
RUN mvn dependency:go-offline -B -pl discovery-server

COPY discovery-server/src discovery-server/src
COPY shared-libs/ shared-libs/

RUN mvn clean package -DskipTests -pl discovery-server

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /app/discovery-server/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]