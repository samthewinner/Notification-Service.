package com.example.SpringKafkaConsumer;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication

public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
//    @Value("${spring.rabbitmq.host}")
//    private  String s;
    @Bean
    public ApplicationRunner applicationRunner (){
        return args -> {
//            System.out.println("---------------------------------------------------"+s+"\n-----------------------------------------");
        };
    }

}
