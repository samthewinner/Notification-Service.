FROM eclipse-temurin:17
RUN mkdir -p /app
WORKDIR /app
COPY ./target/*.jar .
ENTRYPOINT ["java" , "-jar","Task-Creator-0.0.1-SNAPSHOT.jar"]