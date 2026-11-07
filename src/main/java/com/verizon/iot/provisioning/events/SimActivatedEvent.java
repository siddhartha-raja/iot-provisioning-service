package com.verizon.iot.provisioning.events;

import java.time.Instant;
import java.util.List;

/** Published to iot.sim.activated. */
public record SimActivatedEvent(
        String orderId,
        String reservationId,
        String customerId,
        String accountNumber,
        String ratePlanCode,
        List<String> iccids,
        Instant activatedAt) {
}
