# Use a lightweight Java 17 base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the Spring Boot jar
COPY target/cloudbook-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]