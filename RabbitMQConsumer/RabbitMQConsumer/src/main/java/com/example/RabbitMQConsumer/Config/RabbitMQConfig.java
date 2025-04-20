package com.example.RabbitMQConsumer.Config;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class RabbitMQConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);


    @Bean public Queue queue()
    {
        return new Queue("task-queue", false);
    }

    @Bean public Exchange exchange()
    {
        return new DirectExchange("exchange-name");
    }

    @Bean
    public Binding binding(Queue queue, Exchange exchange)
    {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("routing-key")
                .noargs();
    }

    @RabbitListener(queues = "task-queue")
    public void receive(JsonNode data){
//        System.out.println("Received: "+message);
//        messageLengths.add(new String(data));
        log.info(data.toString());
    }

}
