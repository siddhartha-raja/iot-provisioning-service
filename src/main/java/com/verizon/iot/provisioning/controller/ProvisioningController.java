package com.verizon.iot.provisioning.controller;

import com.verizon.iot.provisioning.dto.ProvisioningStatusResponse;
import com.verizon.iot.provisioning.service.ProvisioningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/provisioning")
@RequiredArgsConstructor
@Tag(name = "SIM Provisioning")
public class ProvisioningController {

    private final ProvisioningService provisioningService;

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "Per-SIM provisioning status for an order")
    public ProvisioningStatusResponse getOrderStatus(@PathVariable String orderId) {
        return provisioningService.getStatusByOrder(orderId);
    }

    @PostMapping("/sims/{iccid}/suspend")
    @Operation(summary = "Suspend a SIM on the carrier network")
    public Map<String, String> suspend(@PathVariable String iccid) {
        provisioningService.suspendSim(iccid);
        return Map.of("iccid", iccid, "status", "SUSPENDED");
    }

    @PostMapping("/sims/{iccid}/deactivate")
    @Operation(summary = "Deactivate a SIM; emits iot.sim.deactivated")
    public Map<String, String> deactivate(@PathVariable String iccid,
                                          @RequestParam(defaultValue = "manual deactivation") String reason) {
        provisioningService.deactivateSim(iccid, reason);
        return Map.of("iccid", iccid, "status", "DEACTIVATED");
    }
}
