FROM maven:4.0.0-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jre-slim
WORKDIR /app
COPY --from=build /app/target/CRUD-User-1.0-SNAPSHOT.jar app.jar
RUN mkdir -p logs
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]