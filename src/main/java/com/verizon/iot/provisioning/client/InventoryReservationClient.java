package com.verizon.iot.provisioning.client;

import com.verizon.iot.provisioning.dto.ReservationDetailsDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/** Sync-ish (blocking) call into iot-inventory-service via WebClient. */
@Component
public class InventoryReservationClient {

    private final WebClient inventoryWebClient;

    public InventoryReservationClient(WebClient inventoryWebClient) {
        this.inventoryWebClient = inventoryWebClient;
    }

    public ReservationDetailsDto getReservation(String reservationId) {
        return inventoryWebClient.get()
                .uri("/api/v1/inventory/reservations/{reservationId}", reservationId)
                .retrieve()
                .bodyToMono(ReservationDetailsDto.class)
                .block();
    }
}
