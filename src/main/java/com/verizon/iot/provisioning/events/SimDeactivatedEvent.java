package com.verizon.iot.provisioning.events;

import java.time.Instant;
import java.util.List;

/** Published to iot.sim.deactivated. */
public record SimDeactivatedEvent(String orderId, List<String> iccids, String reason, Instant deactivatedAt) {
}
