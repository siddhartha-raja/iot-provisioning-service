package com.verizon.iot.provisioning.messaging;

import com.verizon.iot.provisioning.events.InventoryReservedEvent;
import com.verizon.iot.provisioning.events.OrderCancelledEvent;
import com.verizon.iot.provisioning.service.ProvisioningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProvisioningEventConsumer {

    private final ProvisioningService provisioningService;

    /** Step 3 of the flow: SIMs are reserved, so activate them on the carrier network. */
    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 2000, multiplier = 2.0), dltTopicSuffix = ".dlt")
    @KafkaListener(topics = "${app.kafka.topics.inventory-reserved}",
                   groupId = "${spring.kafka.consumer.group-id}")
    public void onInventoryReserved(InventoryReservedEvent event) {
        log.info("Received inventory.reserved orderId={} reservationId={}", event.orderId(), event.reservationId());
        provisioningService.provisionOrder(event);
    }

    @DltHandler
    public void onInventoryReservedDlt(InventoryReservedEvent event) {
        log.error("Giving up on provisioning orderId={}", event.orderId());
        provisioningService.failOrder(event.orderId(), event.reservationId(), "Retries exhausted (DLT)");
    }

    /** Customer cancelled: deactivate whatever was activated. */
    @KafkaListener(topics = "${app.kafka.topics.order-cancelled}",
                   groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderCancelled(OrderCancelledEvent event) {
        log.info("Received order.cancelled orderId={}", event.orderId());
        provisioningService.deprovisionOrder(event.orderId(), event.reason());
    }
}
