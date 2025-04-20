package com.example.demo.repository;

import com.example.demo.model.Subscriber;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubscriberRepository extends MongoRepository<Subscriber,String> {
    Subscriber findById(long subscriberId);
}
