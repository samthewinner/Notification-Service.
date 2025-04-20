package com.example.demo.controller;

import com.example.demo.model.Subscriber;

import com.example.demo.model.NotificationTopic;
import com.example.demo.repository.SubscriberRepository;
import com.example.demo.repository.TopicRepository;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.cluster.ClusterMetrics;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.ClientConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;



@RestController()
@RequestMapping("/api")
public class MetaDataMaster {

    @Value("${ignite.cache.name}")
    private String cacheName ;

    private IgniteClient igniteClient;


    public MetaDataMaster(IgniteClient igniteClient){
        this.igniteClient = igniteClient;
    }

    private IgniteCache<Integer, String> cache;

    @Autowired(required = false)
    private NotificationTopic NotificationTopic;

    @Autowired(required = false)
    private Subscriber subscriber;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @GetMapping("/internal/getCacheDetails")
    public ResponseEntity<String> getCacheDetails(){
        Ignition.setClientMode(true);
        try (Ignite ignite = Ignition.start()) { // Replace with your config file
            StringBuilder result = new StringBuilder();

            // Iterate through all server nodes
            for (ClusterNode node : ignite.cluster().forServers().nodes()) {
                // Append basic node information
                result.append("Node ID: ").append(node.id()).append("\n");
                result.append("Addresses: ").append(node.addresses()).append("\n");
                result.append("Is Client: ").append(node.isClient()).append("\n");
                result.append("Node Order: ").append(node.order()).append("\n");
                result.append("Node Version: ").append(node.version()).append("\n");


                // Append node metrics
                ClusterMetrics metrics = node.metrics();
                result.append("Metrics:\n");
                result.append("  CPU Usage: ").append(metrics.getCurrentCpuLoad() * 100).append("%\n");
                result.append("  Heap Memory Used: ").append(metrics.getHeapMemoryUsed() / 1024 / 1024).append(" MB\n");
                result.append("  Non-Heap Memory Used: ").append(metrics.getNonHeapMemoryUsed() / 1024 / 1024).append(" MB\n");
                result.append("  Total Executed Jobs: ").append(metrics.getTotalExecutedJobs()).append("\n");
                result.append("  Active Jobs: ").append(metrics.getCurrentActiveJobs()).append("\n");

                // Add a separator between nodes
                result.append("----------------------------------------\n");
            }

            // Print the final string (or return it as output)
            System.out.println(result.toString());
            return new ResponseEntity<>(result.toString(),HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Error",HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/internal/getAllTopics")
    public  ResponseEntity<List<NotificationTopic>> getAllTopics(){
        return new ResponseEntity<>(topicRepository.findAll(),HttpStatus.OK);
    }

    @GetMapping("/getSubscribers")
    public ResponseEntity<Map<String, Object>> getSubscribers(@RequestParam String topicName){
        System.out.println("Received: "+topicName);
        //find in cache first
        ClientCache<String,NotificationTopic> cache = igniteClient.cache(cacheName);
        NotificationTopic topic1 = cache.get(topicName);
        if(topic1 == null)
        {
            System.out.println("Querying database");
            //query database
           topic1 =  topicRepository.findByName(topicName);
           //not present
           if(null == topic1)
           {
               System.out.println("Not present in db");
               return new ResponseEntity<>(null,HttpStatus.CONFLICT);
           }
            System.out.println(topic1.getSubscribersIds());
           cache.put(topicName,topic1);

        }

        Map<String,Object> res = new HashMap<>();

        res.put("subscribers",topic1.getSubscribersIds());

        return new ResponseEntity<>(res,HttpStatus.OK);

    }

    @PostMapping("/createTopic")
    public ResponseEntity<String> createTopic(@RequestBody NotificationTopic newTopic){
        ClientCache<String,NotificationTopic> cache = igniteClient.cache(cacheName);
        NotificationTopic topic1 = cache.get(newTopic.getName());
        if(null == topic1)
        {
            //look in db
            topic1 = topicRepository.findByName(newTopic.getName());
            if(null == topic1)
            {
                 topic1 = topicRepository.save(newTopic);
                 return new ResponseEntity<>("Voila! New topic created", HttpStatus.OK);

            }

        }
        return new ResponseEntity<>("Topic already exists",HttpStatus.CONFLICT);
    }

    @PostMapping("/createSubscriber")
    public ResponseEntity<String> createSubscriber(@RequestBody Subscriber newSub){
        if(newSub == null)
            return new ResponseEntity<>("Null subscriber provided",HttpStatus.BAD_REQUEST);

        ClientCache<Long,Subscriber> cache = igniteClient.cache(cacheName);
        Subscriber subscriber1 = cache.get(newSub.getId());
        if(null == subscriber1){
            //TODO: Make these async
            cache.put(newSub.getId(),newSub);
            subscriberRepository.save(newSub);
            return new ResponseEntity<>("Subscriber added",HttpStatus.OK);
        }
        return new ResponseEntity<>("Already exists",HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestParam String currTopic , @RequestParam Long subscriberId ){
        ClientCache<String,NotificationTopic> cache = igniteClient.cache(cacheName);
        NotificationTopic topic1 = cache.get(currTopic);

        if(null == topic1){
            topic1 =  topicRepository.findByName(currTopic);
            if(null == topic1)
                return new ResponseEntity<>("No topic found",HttpStatus.BAD_REQUEST);

        }

        topic1.getSubscribersIds().add(subscriberId);

        //TODO: make this an async call
        cache.put(currTopic,topic1);
        topicRepository.save(topic1);

        return new ResponseEntity<>("Subscribed to "+currTopic,HttpStatus.OK);
    }
    
    @DeleteMapping("/subscribe")
    public ResponseEntity<String> deleteSubscriber(@RequestParam long subscriberId){
        Subscriber subscriber1 = subscriberRepository.findById(subscriberId);
        if(subscriber1 == null)
            return new ResponseEntity<>("No subscriber exists",HttpStatus.BAD_REQUEST);
        subscriberRepository.delete(subscriber1);
        ClientCache<Long,Subscriber> cache = igniteClient.cache(cacheName);
        if(cache.containsKey(subscriberId))
            cache.clear(subscriberId);

        return new ResponseEntity<>("Subscriber with id: "+subscriberId +" deleted",HttpStatus.OK);
    }

}