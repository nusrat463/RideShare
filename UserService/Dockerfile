# ---------- Stage 1: Build the application ----------
FROM maven:3.8.5-openjdk-8-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven pom.xml file to the container
COPY UserService/pom.xml ./

# Download the Maven dependencies (this step caches dependencies for faster builds)
RUN mvn dependency:go-offline

# Now copy the entire content of UserService project into the container
COPY UserService/ ./UserService/

# Build the JAR file (skip tests for faster build)
RUN mvn clean package -DskipTests

# ---------- Stage 2: Run the application ----------
FROM openjdk:8-jdk-slim

# Set the working directory for the running container
WORKDIR /app

# Copy the JAR file from the build stage into the final container
COPY --from=build /app/UserService/target/ride-sharing-0.0.1-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
