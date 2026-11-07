package com.verizon.iot.provisioning.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "provisioning_records")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProvisioningRecord {

    @Id
    private String provisioningId;
    private String orderId;
    private String reservationId;
    private String iccid;
    private String ratePlanCode;
    private String carrierRequestId;

    @Enumerated(EnumType.STRING)
    private ProvisioningStatus status;

    private String failureReason;
    private Instant createdAt;
    private Instant updatedAt;
}
