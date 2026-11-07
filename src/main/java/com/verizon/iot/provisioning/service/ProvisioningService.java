package com.verizon.iot.provisioning.service;

import com.verizon.iot.provisioning.client.BillingRatePlanClient;
import com.verizon.iot.provisioning.client.InventoryReservationClient;
import com.verizon.iot.provisioning.client.ThingSpaceClient;
import com.verizon.iot.provisioning.dto.ProvisioningStatusResponse;
import com.verizon.iot.provisioning.dto.RatePlanDto;
import com.verizon.iot.provisioning.dto.ReservationDetailsDto;
import com.verizon.iot.provisioning.dto.ThingSpaceActionRequest;
import com.verizon.iot.provisioning.dto.ThingSpaceActionResponse;
import com.verizon.iot.provisioning.entity.ProvisioningRecord;
import com.verizon.iot.provisioning.entity.ProvisioningStatus;
import com.verizon.iot.provisioning.events.InventoryReservedEvent;
import com.verizon.iot.provisioning.events.ProvisioningFailedEvent;
import com.verizon.iot.provisioning.events.SimActivatedEvent;
import com.verizon.iot.provisioning.events.SimDeactivatedEvent;
import com.verizon.iot.provisioning.exception.SimNotFoundException;
import com.verizon.iot.provisioning.messaging.ProvisioningEventPublisher;
import com.verizon.iot.provisioning.repository.ProvisioningRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProvisioningService {

    private final ProvisioningRecordRepository recordRepository;
    private final BillingRatePlanClient billingClient;
    private final InventoryReservationClient inventoryClient;
    private final ThingSpaceClient thingSpaceClient;
    private final ProvisioningEventPublisher eventPublisher;

    /**
     * Activate every SIM in the reservation:
     *  1. validate rate plan  (REST -> billing-service)
     *  2. fetch ICCIDs         (REST -> inventory-service)
     *  3. activate on carrier  (REST -> ThingSpace, external)
     *  4. emit sim.activated   (Kafka)
     */
    @Transactional
    public void provisionOrder(InventoryReservedEvent event) {
        RatePlanDto plan = billingClient.getRatePlan(event.ratePlanCode());
        if (plan == null || !plan.active()) {
            failOrder(event.orderId(), event.reservationId(), "Rate plan invalid or inactive: " + event.ratePlanCode());
            return;
        }

        ReservationDetailsDto reservation = inventoryClient.getReservation(event.reservationId());
        List<String> activated = new ArrayList<>();
        try {
            for (String iccid : reservation.iccids()) {
                ThingSpaceActionResponse resp = thingSpaceClient.activateDevice(
                        new ThingSpaceActionRequest(iccid, plan.code(), event.accountNumber()));
                recordRepository.save(ProvisioningRecord.builder()
                        .provisioningId("PRV-" + UUID.randomUUID())
                        .orderId(event.orderId())
                        .reservationId(event.reservationId())
                        .iccid(iccid)
                        .ratePlanCode(plan.code())
                        .carrierRequestId(resp.requestId())
                        .status(ProvisioningStatus.ACTIVE)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build());
                activated.add(iccid);
            }
        } catch (RuntimeException ex) {
            log.error("Carrier activation failed orderId={}", event.orderId(), ex);
            failOrder(event.orderId(), event.reservationId(), "Carrier error: " + ex.getMessage());
            return;
        }

        eventPublisher.publishSimActivated(new SimActivatedEvent(
                event.orderId(), event.reservationId(), event.customerId(), event.accountNumber(),
                event.ratePlanCode(), activated, Instant.now()));
    }

    @Transactional
    public void failOrder(String orderId, String reservationId, String reason) {
        eventPublisher.publishProvisioningFailed(
                new ProvisioningFailedEvent(orderId, reservationId, reason, Instant.now()));
    }

    /** Triggered by iot.order.cancelled or manual API call. */
    @Transactional
    public void deprovisionOrder(String orderId, String reason) {
        List<ProvisioningRecord> records = recordRepository.findByOrderId(orderId);
        List<String> iccids = new ArrayList<>();
        for (ProvisioningRecord rec : records) {
            if (rec.getStatus() == ProvisioningStatus.DEACTIVATED) {
                continue;
            }
            thingSpaceClient.deactivateDevice(new ThingSpaceActionRequest(rec.getIccid(), rec.getRatePlanCode(), null));
            rec.setStatus(ProvisioningStatus.DEACTIVATED);
            rec.setUpdatedAt(Instant.now());
            recordRepository.save(rec);
            iccids.add(rec.getIccid());
        }
        if (!iccids.isEmpty()) {
            eventPublisher.publishSimDeactivated(new SimDeactivatedEvent(orderId, iccids, reason, Instant.now()));
        }
    }

    @Transactional
    public void suspendSim(String iccid) {
        ProvisioningRecord rec = recordRepository.findByIccid(iccid).orElseThrow(() -> new SimNotFoundException(iccid));
        thingSpaceClient.suspendDevice(new ThingSpaceActionRequest(iccid, rec.getRatePlanCode(), null));
        rec.setStatus(ProvisioningStatus.SUSPENDED);
        rec.setUpdatedAt(Instant.now());
        recordRepository.save(rec);
    }

    @Transactional
    public void deactivateSim(String iccid, String reason) {
        ProvisioningRecord rec = recordRepository.findByIccid(iccid).orElseThrow(() -> new SimNotFoundException(iccid));
        thingSpaceClient.deactivateDevice(new ThingSpaceActionRequest(iccid, rec.getRatePlanCode(), null));
        rec.setStatus(ProvisioningStatus.DEACTIVATED);
        rec.setUpdatedAt(Instant.now());
        recordRepository.save(rec);
        eventPublisher.publishSimDeactivated(
                new SimDeactivatedEvent(rec.getOrderId(), List.of(iccid), reason, Instant.now()));
    }

    @Transactional(readOnly = true)
    public ProvisioningStatusResponse getStatusByOrder(String orderId) {
        List<ProvisioningStatusResponse.SimProvisioningStatus> sims = recordRepository.findByOrderId(orderId).stream()
                .map(r -> new ProvisioningStatusResponse.SimProvisioningStatus(
                        r.getIccid(), r.getStatus().name(), r.getFailureReason()))
                .toList();
        return new ProvisioningStatusResponse(orderId, sims);
    }
}
