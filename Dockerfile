# Use an appropriate base image with Java 17 installed
FROM openjdk:17-jdk-slim-buster

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled JAR file to the container
COPY target/book-rental-system-0.0.1-SNAPSHOT.jar app.jar

# Expose the port that the Spring Boot application listens on
EXPOSE 8080

# Set the command to run the Spring Boot application when the container starts
CMD ["java", "-jar", "app.jar"]
