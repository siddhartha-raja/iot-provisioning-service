package com.verizon.iot.provisioning.client;

import com.verizon.iot.provisioning.dto.ThingSpaceActionRequest;
import com.verizon.iot.provisioning.dto.ThingSpaceActionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** EXTERNAL system: carrier connectivity-management platform (ThingSpace). Not part of this repo. */
@Component
public class ThingSpaceClient {

    private final RestTemplate restTemplate;

    @Value("${clients.thingspace.base-url}")
    private String baseUrl;

    public ThingSpaceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ThingSpaceActionResponse activateDevice(ThingSpaceActionRequest request) {
        return restTemplate.postForObject(baseUrl + "/api/m2m/v1/devices/actions/activate",
                request, ThingSpaceActionResponse.class);
    }

    public ThingSpaceActionResponse suspendDevice(ThingSpaceActionRequest request) {
        return restTemplate.postForObject(baseUrl + "/api/m2m/v1/devices/actions/suspend",
                request, ThingSpaceActionResponse.class);
    }

    public ThingSpaceActionResponse deactivateDevice(ThingSpaceActionRequest request) {
        return restTemplate.postForObject(baseUrl + "/api/m2m/v1/devices/actions/deactivate",
                request, ThingSpaceActionResponse.class);
    }
}
