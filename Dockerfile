FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline

# Copy complete implementation source files and package the application artifact
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Step 2: Runtime stage optimized for security and memory efficiency
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create a non-privileged system user account to run the application securely
RUN addgroup -S rdasgroup && adduser -S rdasuser -G rdasgroup
USER rdasuser

# Copy the compiled binary executable jar file from the build stage environment
COPY --from=build /app/target/*.jar app.jar

# Expose target entry Tomcat container listener port
EXPOSE 8080

# Configure production-optimized JVM flags for containerized microservices
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:+ExitOnOutOfMemoryError", "-jar", "app.jar"]