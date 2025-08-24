# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only pom first to leverage Docker layer caching
COPY pom.xml ./
# Download dependencies (will be cached if pom.xml unchanged)
RUN mvn -q -e -DskipTests dependency:go-offline

# Now copy sources and build
COPY src ./src
RUN mvn -q -Dmaven.test.skip=true package

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# Render provides PORT; Spring Boot reads it via server.port=${PORT:8080}
ENV PORT=8080
EXPOSE 8080

# Copy the fat JAR produced by Spring Boot
COPY --from=build /app/target/procalc-0.0.1.jar /app/app.jar

# Start the app
ENTRYPOINT ["java","-jar","/app/app.jar"]
