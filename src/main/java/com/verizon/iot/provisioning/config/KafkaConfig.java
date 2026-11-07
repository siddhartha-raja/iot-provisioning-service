package com.verizon.iot.provisioning.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic simActivatedTopic(TopicProperties props) {
        return TopicBuilder.name(props.getSimActivated()).partitions(6).replicas(3).build();
    }

    @Bean
    public NewTopic simDeactivatedTopic(TopicProperties props) {
        return TopicBuilder.name(props.getSimDeactivated()).partitions(6).replicas(3).build();
    }

    @Bean
    public NewTopic provisioningFailedTopic(TopicProperties props) {
        return TopicBuilder.name(props.getProvisioningFailed()).partitions(6).replicas(3).build();
    }

    @Bean
    public RecordMessageConverter jsonMessageConverter() {
        return new StringJsonMessageConverter();
    }
}
