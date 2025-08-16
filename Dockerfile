# ---- Build stage ----
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn -DskipTests package

# ---- Run stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render sets PORT; Spring reads server.port=${PORT}
ENV PORT=8080
EXPOSE 8080
CMD ["sh","-c","java -Xms256m -Xmx384m -Dserver.port=${PORT} -jar app.jar"]