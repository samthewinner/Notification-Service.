package com.example.demo.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Topic")
public class NotificationTopic {
    private List<Long>subscribersIds;
    private String name;
    private long id;

    public List<Long> getSubscribersIds() {
        return subscribersIds;
    }

    public void setSubscribersIds(List<Long> subscribersIds) {
        this.subscribersIds = subscribersIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
