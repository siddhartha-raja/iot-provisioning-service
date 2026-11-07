package com.verizon.iot.provisioning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.verizon.iot.provisioning.config.TopicProperties;

@SpringBootApplication
@EnableConfigurationProperties(TopicProperties.class)
public class ProvisioningServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProvisioningServiceApplication.class, args);
    }
}
