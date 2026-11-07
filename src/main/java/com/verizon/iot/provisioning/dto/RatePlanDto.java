package com.verizon.iot.provisioning.dto;

import java.math.BigDecimal;

/** Mirrors billing-service GET /api/v1/billing/rate-plans/{code}. */
public record RatePlanDto(String code, String name, int dataAllowanceMb, BigDecimal monthlyFee, boolean active) {
}
