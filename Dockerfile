# Use official Java image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy pom and source
COPY pom.xml .
COPY src ./src

# Package application
RUN ./mvnw clean package -DskipTests

# Run the jar
CMD ["java", "-jar", "target/stl-core-0.0.1-SNAPSHOT.jar"]

# Expose default port
EXPOSE 8080

