# Use the official Maven image with JDK 21
FROM maven:3.9.6-eclipse-temurin-21-jammy

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the application code
COPY src ./src

# Compile the application
RUN mvn package

# Copy the frontend code
COPY frontend /app/frontend

# Build the frontend
RUN apt-get update && apt-get install -y nodejs npm
WORKDIR /app/frontend
RUN npm install
RUN npm run build

# Set the working directory back to the backend
WORKDIR /app

# Set the entry point to run the Java program
ENTRYPOINT ["mvn", "exec:java", "-Dexec.mainClass=edu.usfca.cs272.Driver", "-Dexec.args=-html 'https://usf-cs272-spring2024.github.io/project-web/docs/api/allclasses-index.html' -crawl 50 -threads 3 -server 3000"]
