# syntax=docker/dockerfile:1

# Build stage: compile just this module, using the reactor's parent pom for version management
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Copy only the poms first so dependency resolution is cached across rebuilds
# whenever auth-service source (but not pom.xml) changes.
COPY pom.xml .
COPY auth-service/pom.xml auth-service/pom.xml
COPY ecommerce-service/pom.xml ecommerce-service/pom.xml
COPY payment-service/pom.xml payment-service/pom.xml
RUN mvn -q -pl auth-service dependency:go-offline

COPY auth-service/src auth-service/src
RUN mvn -q -pl auth-service package -DskipTests

# Runtime stage: just the JRE and the built jar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/auth-service/target/auth-service-*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
