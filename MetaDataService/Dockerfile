FROM eclipse-temurin:17
RUN mkdir -p /app
WORKDIR /app
COPY ./target/MetaData-Service-0.0.1-SNAPSHOT.jar .
COPY run.sh .
RUN chmod +x run.sh
ENTRYPOINT ["./run.sh"]