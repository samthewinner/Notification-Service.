package com.example.RabbitMQConsumer.Consumer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@RabbitListener(queues = "task-queue")
public class RabbitMQConsumer {

    @RabbitHandler
    public void receive(String message){
        System.out.println("Received: "+message);
    }

}
