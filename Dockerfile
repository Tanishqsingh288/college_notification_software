# Use OpenJDK 17 slim image
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the Spring Boot jar
COPY target/notificationsystem-0.0.1-SNAPSHOT.jar app.jar

# Expose backend port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java","-jar","app.jar"]
