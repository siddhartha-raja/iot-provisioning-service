package com.verizon.iot.provisioning.dto;

import java.util.List;

/** Mirrors inventory-service GET /api/v1/inventory/reservations/{id}. */
public record ReservationDetailsDto(String reservationId, String orderId, String status, List<String> iccids) {
}
