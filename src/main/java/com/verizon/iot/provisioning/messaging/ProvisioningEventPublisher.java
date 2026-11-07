package com.verizon.iot.provisioning.messaging;

import com.verizon.iot.provisioning.config.TopicProperties;
import com.verizon.iot.provisioning.events.ProvisioningFailedEvent;
import com.verizon.iot.provisioning.events.SimActivatedEvent;
import com.verizon.iot.provisioning.events.SimDeactivatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProvisioningEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TopicProperties topics;

    public void publishSimActivated(SimActivatedEvent event) {
        log.info("Publishing sim.activated orderId={} count={}", event.orderId(), event.iccids().size());
        kafkaTemplate.send(topics.getSimActivated(), event.orderId(), event);
    }

    public void publishProvisioningFailed(ProvisioningFailedEvent event) {
        log.warn("Publishing provisioning.failed orderId={}", event.orderId());
        kafkaTemplate.send(topics.getProvisioningFailed(), event.orderId(), event);
    }

    public void publishSimDeactivated(SimDeactivatedEvent event) {
        log.info("Publishing sim.deactivated orderId={}", event.orderId());
        kafkaTemplate.send(topics.getSimDeactivated(), event.orderId(), event);
    }
}
