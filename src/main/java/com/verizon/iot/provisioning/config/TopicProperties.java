package com.verizon.iot.provisioning.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Binds app.kafka.topics.* from application.yml (type-safe topic names for producers). */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.kafka.topics")
public class TopicProperties {
    private String inventoryReserved;
    private String orderCancelled;
    private String simActivated;
    private String simDeactivated;
    private String provisioningFailed;
}
