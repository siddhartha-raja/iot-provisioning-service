package com.verizon.iot.provisioning.events;

import java.time.Instant;

/** Consumed from iot.inventory.reserved. */
public record InventoryReservedEvent(
        String orderId,
        String reservationId,
        String customerId,
        String accountNumber,
        String simType,
        int quantity,
        String ratePlanCode,
        Instant reservedAt) {
}
