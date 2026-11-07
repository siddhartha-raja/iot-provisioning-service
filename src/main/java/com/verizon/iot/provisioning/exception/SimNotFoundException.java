package com.verizon.iot.provisioning.exception;

public class SimNotFoundException extends RuntimeException {
    public SimNotFoundException(String iccid) {
        super("No provisioning record for ICCID: " + iccid);
    }
}
