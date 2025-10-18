FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Install bash (needed for mvnw in Alpine)
RUN apk add --no-cache bash

# Copy Maven wrapper and pom
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src

# Make sure mvnw is executable
RUN chmod +x mvnw

# Package the application
RUN ./mvnw clean package -DskipTests

# Run the jar
CMD ["java", "-jar", "target/stl-core-0.0.1-SNAPSHOT.jar"]

EXPOSE 8080

