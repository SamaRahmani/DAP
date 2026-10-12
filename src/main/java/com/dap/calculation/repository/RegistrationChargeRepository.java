package com.dap.calculation.repository;

import com.dap.calculation.entity.RegistrationCharge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationChargeRepository
        extends JpaRepository<RegistrationCharge, Long> {
    List<RegistrationCharge> findByStateCode(String stateCode);
}
