package com.verizon.iot.provisioning.dto;

import java.util.List;

public record ProvisioningStatusResponse(String orderId, List<SimProvisioningStatus> sims) {

    public record SimProvisioningStatus(String iccid, String status, String failureReason) {
    }
}
