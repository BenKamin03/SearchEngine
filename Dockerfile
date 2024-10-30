# Stage 1: Build the Java application using Maven
FROM maven:3.8.6-openjdk-11 AS build

# Set the working directory for the Maven build
WORKDIR /app

# Copy the pom.xml file to download dependencies
COPY backend/pom.xml ./

# Copy the source code
COPY backend/src ./src

# Build the application (this will create a JAR file)
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM openjdk:11-jre-slim

# Set the working directory for the backend
WORKDIR /app

# Copy the JAR file from the previous stage (replace `your-app.jar` with the actual JAR file name)
COPY --from=build /app/target/*.jar your-app.jar

# Expose the port your application runs on (adjust as necessary)
EXPOSE 3000

# Start the Java application with the desired arguments
CMD ["java", "-jar", "your-app.jar", "-html", "https://usf-cs272-spring2024.github.io/project-web/docs/api/allclasses-index.html", "-crawl", "50", "-threads", "3", "-server", "3000"]