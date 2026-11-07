package com.verizon.iot.provisioning.client;

import com.verizon.iot.provisioning.dto.RatePlanDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** Sync call into iot-billing-service via RestTemplate to validate the rate plan before activation. */
@Component
public class BillingRatePlanClient {

    private final RestTemplate restTemplate;
    private final String billingBaseUrl;

    public BillingRatePlanClient(RestTemplate restTemplate,
                                 @Value("${clients.billing.base-url}") String billingBaseUrl) {
        this.restTemplate = restTemplate;
        this.billingBaseUrl = billingBaseUrl;
    }

    public RatePlanDto getRatePlan(String ratePlanCode) {
        String url = billingBaseUrl + "/api/v1/billing/rate-plans/{code}";
        return restTemplate.getForObject(url, RatePlanDto.class, ratePlanCode);
    }
}
