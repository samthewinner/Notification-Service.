package com.example.demo.repository;
import org.apache.ignite.Ignition;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.ClientConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IgniteThinClientConfig {

    @Value("${ignite.cluster.addresses}")
    private String igniteNodeIps;

    @Bean
    public IgniteClient igniteClient () {
        System.out.println("Ignite-node: "+igniteNodeIps);
        ClientConfiguration cfg = new ClientConfiguration()
                .setAddresses(igniteNodeIps)
                .setPartitionAwarenessEnabled(true); // Connect to local Ignite server
        try{
            IgniteClient ret = Ignition.startClient(cfg);
            return ret;
        }
        catch (Exception e){
            System.out.println("Error creating obj: " +e.getMessage());
        }
            return null;

    }
}