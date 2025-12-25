# Step 1 - Build the JAR using Maven
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -q -e dependency:go-offline

COPY src ./src
RUN mvn -q -e package -DskipTests

# Step 2 - Run the JAR using Java
FROM eclipse-temurin:21
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
