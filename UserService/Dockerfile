# Stage 1: Build the application
FROM maven:3.8.4-openjdk-11-slim AS build

# Copy only the necessary files (pom.xml and source code)
COPY . .

# Run Maven to build the project
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:11-jre-slim

# Copy the jar file from the build stage
COPY --from=build /target/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
