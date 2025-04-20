package com.example.SpringKafkaConsumer.KafkaConsumer;

import com.example.SpringKafkaConsumer.RabbitMQProducer.RabbitMQProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Service
class RabbitMQTask implements Runnable{

    private String name;
    static int BATCHSIZE;
    private int start;
    List<Long> subscriber;
    private String message;
    private RabbitMQProducer rabbitMQProducer;
//    @Autowired
    private ObjectMapper objectMapper;
//
//    @Autowired
//    private RabbitMQProducer rabbitMQProducer;



    RabbitMQTask(int start,List<Long>subscriber,String message,ObjectMapper objectMapper,RabbitMQProducer rabbitMQProducer){
        this.subscriber = subscriber;
        this.start = start;
        this.message = message;
        this.objectMapper = objectMapper;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    {
        BATCHSIZE = 100;
    }


    @Override
    public void run() {
        List<Long> batch = new ArrayList<>(BATCHSIZE);
        for(int i=this.start;i<Math.min(this.start+BATCHSIZE,subscriber.size());i++)
            batch.add(subscriber.get(i));
        Map<String,Object> msg = new HashMap<>();
        msg.put("subscribers",batch);
        msg.put("message",message);
        rabbitMQProducer.sendMessage(objectMapper.valueToTree(msg));
        System.out.println("Message sent to RabbitMQ");
    }
}
