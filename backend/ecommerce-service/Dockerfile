# syntax=docker/dockerfile:1

# Build stage: compile just this module, using the reactor's parent pom for version management
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Copy only the poms first so dependency resolution is cached across rebuilds
# whenever ecommerce-service source (but not pom.xml) changes.
COPY pom.xml .
COPY auth-service/pom.xml auth-service/pom.xml
COPY ecommerce-service/pom.xml ecommerce-service/pom.xml
COPY payment-service/pom.xml payment-service/pom.xml
RUN mvn -q -pl ecommerce-service dependency:go-offline

COPY ecommerce-service/src ecommerce-service/src
RUN mvn -q -pl ecommerce-service package -DskipTests

# Runtime stage: just the JRE and the built jar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/ecommerce-service/target/ecommerce-service-*.jar app.jar

EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
