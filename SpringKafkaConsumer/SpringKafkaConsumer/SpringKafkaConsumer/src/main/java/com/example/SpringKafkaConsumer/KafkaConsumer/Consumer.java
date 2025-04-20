package com.example.SpringKafkaConsumer.KafkaConsumer;

import com.example.SpringKafkaConsumer.RabbitMQProducer.RabbitMQProducer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.InstanceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.core.type.TypeReference;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class Consumer {

    @Autowired
    TaskExecutor taskExecutor;

    @Autowired
    ObjectMapper objectMapper;

    private final DiscoveryClient discoveryClient;
    private final RestClient restClient;

    private static int BATCHSIZE = 100;

    public Consumer (DiscoveryClient discoveryClient, RestClient.Builder restClientBuilder) {
        this.discoveryClient = discoveryClient;
        restClient = restClientBuilder.build();
    }

    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    @KafkaListener(topics = {"topic1"},groupId = "gid1")
    public void consumeKafkaMessage(String message)  {

//        System.out.println(message.replace("\"","\\\""));
        JsonNode data = null;
        try {
            data = objectMapper.readTree(message);
        } catch (JsonProcessingException e) {
            System.out.println("Problem in parsing JSON");
            throw new RuntimeException(e);
        }
        if(data == null){
            System.out.println("NULL data");
        }

        //System.out.println(data.toString());
        //fetch the recepients from metadata service
//        String uri = "http://localhost:8083/api/getSubscribers?topicName="+message;

        ServiceInstance metadataServiceInstance = discoveryClient.getInstances("Metadata-Service").get(0);

        String url = metadataServiceInstance.getUri().toString();
        String topicName = data.get("TopicName").toString();
        String urlSuffix = "/api/getSubscribers?topicName="+topicName.substring(1,topicName.length()-1);

        url += urlSuffix;

        System.out.println(url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {

            // Get the response body
            JsonNode res = objectMapper.readTree(response.body().string());

//            System.out.println("Json: "+res);
            System.out.println(res.toString());
            List<Long> subscribers = objectMapper.convertValue(res.get("subscribers"), new TypeReference<List<Long>>() {});
            System.out.println(subscribers);

            //response contains a list of subscribers which needs to broken down into batches

            for(int i=0;i<subscribers.size();i+=BATCHSIZE){
                  //create tasks and submit them to the executor
                final int start = i;
                taskExecutor.execute(new RabbitMQTask(start,subscribers,data.get("message").toString(),objectMapper,rabbitMQProducer));
//                List<Long> batch = new ArrayList<>();
//                for(int j=0;j<Math.min(subscribers.size(),i+100);j++){
//                    batch.add(subscribers.get(i+j));
//                }
//                Map<String,Object> msg = new HashMap<>();
//                msg.put("subscribers",batch);
//                msg.put("message",data.get("message"));
//                rabbitMQProducer.sendMessage(objectMapper.valueToTree(msg));
            }

            //create batches


        } catch (Exception e) {
            e.printStackTrace();
        }

//            rabbitMQProducer.sendMessage(message);

        //create tasks and push into rabbitMQ

    }
}
