
package com.dap.calculation.repository;

import com.dap.calculation.entity.RegistrationCharge;
import com.dap.calculation.enums.ChargeScope;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationChargeRepository
        extends JpaRepository<RegistrationCharge, Long> {

    // Retrieve all charges for a state
    List<RegistrationCharge> findByStateCode(String stateCode);

    // Retrieve state-level charges for a state
    List<RegistrationCharge> findByStateCodeAndChargeScope(
            String stateCode,
            ChargeScope chargeScope
    );

    // Retrieve all charges for a city
    List<RegistrationCharge> findByCityId(Long cityId);

    // Retrieve city-level charges for a city and scope
    List<RegistrationCharge> findByCityIdAndChargeScope(
            Long cityId,
            ChargeScope chargeScope
    );

    // Retrieve all national-level charges
    List<RegistrationCharge> findByChargeScope(ChargeScope chargeScope);
}
