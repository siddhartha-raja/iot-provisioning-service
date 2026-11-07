package com.verizon.iot.provisioning.repository;

import com.verizon.iot.provisioning.entity.ProvisioningRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProvisioningRecordRepository extends JpaRepository<ProvisioningRecord, String> {
    List<ProvisioningRecord> findByOrderId(String orderId);
    Optional<ProvisioningRecord> findByIccid(String iccid);
}
