FROM eclipse-temurin:21-jre

LABEL authors="edwin" \
      description="Protocol Proxy - Multi-Protocol Message Proxy Application"

WORKDIR /app

# Copy the built JAR file
COPY target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
