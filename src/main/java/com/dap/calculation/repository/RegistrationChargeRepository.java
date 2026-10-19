package com.dap.calculation.repository;

import com.dap.calculation.entity.RegistrationCharge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationChargeRepository
        extends JpaRepository<RegistrationCharge, Long> {

    // Retrieve all charges for a state
    List<RegistrationCharge> findByStateCode(String stateCode);

    // Retrieve state-level charges for a state
    List<RegistrationCharge> findByStateCodeAndChargeScope(
            String stateCode,
            String chargeScope
    );

    // Retrieve city-level charges for a city
    List<RegistrationCharge> findByCityId(Long cityId);

    // Retrieve city-level charges for a city and scope
    List<RegistrationCharge> findByCityIdAndChargeScope(
            Long cityId,
            String chargeScope
    );
}