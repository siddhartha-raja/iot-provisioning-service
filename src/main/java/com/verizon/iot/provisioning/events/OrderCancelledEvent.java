package com.verizon.iot.provisioning.events;

import java.time.Instant;

/** Consumed from iot.order.cancelled. */
public record OrderCancelledEvent(String orderId, String reason, Instant cancelledAt) {
}
