FROM eclipse-temurin:11-jre
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
