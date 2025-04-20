package com.example.demo.repository;

import com.example.demo.model.NotificationTopic;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface TopicRepository  extends  MongoRepository<NotificationTopic,String> {
    NotificationTopic findByName(String name);
}
