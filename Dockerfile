# ---- Stage 1: build ----
# Full JDK + Maven. This image is big, but it only exists while building.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the dependency list first and download it, so that editing your Java
# source does not force Maven to re-download every library again.
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Now copy the source and produce the runnable JAR.
COPY src ./src
RUN mvn -q package -DskipTests

# ---- Stage 2: run ----
# Only a JRE. No compiler, no source code, no Maven.
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/university-management-system-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
