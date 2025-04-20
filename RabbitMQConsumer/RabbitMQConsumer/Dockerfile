FROM eclipse-temurin:17
RUN mkdir -p /opt/app
WORKDIR /opt/app
COPY ./target/RabbitMQConsumer-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java" , "-jar" , "RabbitMQConsumer-0.0.1-SNAPSHOT.jar"]