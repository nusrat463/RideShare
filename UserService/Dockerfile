# Stage 1: Build the application
FROM maven:3.8.4-openjdk-11-slim AS build

WORKDIR /app
# Copy only the necessary files (pom.xml and source code)
COPY . .
COPY ./pom.xml ./

# Run Maven to build the project
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:11-jre-slim

WORKDIR /app
# Copy the jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
