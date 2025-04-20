package com.example.SpringKafkaConsumer.RabbitMQProducer;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RabbitMQProducer {

    @Autowired private RabbitTemplate rabbitTemplate;

    public void sendMessage(JsonNode message)
    {
        rabbitTemplate.convertAndSend(
                "exchange-name", "routing-key", message);
    }
}

