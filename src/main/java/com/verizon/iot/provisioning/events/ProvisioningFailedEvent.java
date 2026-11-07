package com.verizon.iot.provisioning.events;

import java.time.Instant;

/** Published to iot.provisioning.failed. */
public record ProvisioningFailedEvent(String orderId, String reservationId, String reason, Instant failedAt) {
}
