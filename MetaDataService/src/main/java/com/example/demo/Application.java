package com.example.demo;

import java.util.Arrays;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})

public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
//	@Value("${spring.data.mongodb.uri}")
//	private String s;
	@Bean
	public ApplicationRunner applicationRunner(){
		return args -> {
//			System.out.println(s);

//			Ignite ignite = Ignition.start();
//			IgniteCache<Integer,String>m = ignite.getOrCreateCache("CacheUno");
//			m.put(1,"Hello");
//			m.put(2,"World");
//			System.out.println(m.get(1)+" "+m.get(2));

		};
	}

}