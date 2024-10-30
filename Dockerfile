# Stage 1: Build the React frontend
FROM node:18 AS frontend-build

# Set the working directory for the frontend build
WORKDIR /app/frontend

# Copy the frontend package.json and package-lock.json
COPY frontend/package*.json ./

# Install the frontend dependencies
RUN npm install

# Copy the rest of the frontend source code
COPY frontend/ .

# Build the frontend
RUN npm run build

# Stage 2: Build the Java application using Maven
FROM maven:3.8.6-openjdk-11 AS backend-build

# Set the working directory for the Maven build
WORKDIR /app/src

# Copy the pom.xml file to download dependencies
COPY src/pom.xml ./

# Copy the source code
COPY src ./src

# Build the backend application (this will download dependencies and compile the code)
RUN mvn clean compile

# Stage 3: Create the final image
FROM openjdk:11-jre-slim

# Set the working directory for the backend
WORKDIR /app

# Copy the compiled classes from the backend build stage
COPY --from=backend-build /app/src/target/classes ./classes

# Copy the built frontend files from the frontend build stage
COPY --from=frontend-build /app/frontend/build ./frontend/build

# Expose the port your application runs on
EXPOSE 3000

# Start the Java application with the desired arguments
CMD ["java", "-cp", "classes", "edu.usfca.cs272.Driver", "-html", "https://usf-cs272-spring2024.github.io/project-web/docs/api/allclasses-index.html", "-crawl", "50", "-threads", "3", "-server", "3000"]