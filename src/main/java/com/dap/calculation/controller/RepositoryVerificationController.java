package com.dap.calculation.controller;

import com.dap.calculation.repository.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/diagnostics")
public class RepositoryVerificationController {

    private final VehicleRepository vehicleRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final RtoRepository rtoRepository;
    private final InsuranceRepository insuranceRepository;
    private final TaxTypeRepository taxTypeRepository;
    private final TaxRuleRepository taxRuleRepository;
    private final RegistrationChargeRepository registrationChargeRepository;

    public RepositoryVerificationController(
            VehicleRepository vehicleRepository,
            StateRepository stateRepository,
            CityRepository cityRepository,
            RtoRepository rtoRepository,
            InsuranceRepository insuranceRepository,
            TaxTypeRepository taxTypeRepository,
            TaxRuleRepository taxRuleRepository,
            RegistrationChargeRepository registrationChargeRepository) {

        this.vehicleRepository = vehicleRepository;
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
        this.rtoRepository = rtoRepository;
        this.insuranceRepository = insuranceRepository;
        this.taxTypeRepository = taxTypeRepository;
        this.taxRuleRepository = taxRuleRepository;
        this.registrationChargeRepository = registrationChargeRepository;
    }

    @GetMapping("/master-data-counts")
    public Map<String, Long> getMasterDataCounts() {

        Map<String, Long> counts = new LinkedHashMap<>();

        counts.put("vehicles", vehicleRepository.count());
        counts.put("states", stateRepository.count());
        counts.put("cities", cityRepository.count());
        counts.put("rtos", rtoRepository.count());
        counts.put("insuranceProviders", insuranceRepository.count());
        counts.put("taxTypes", taxTypeRepository.count());
        counts.put("taxRules", taxRuleRepository.count());
        counts.put("registrationCharges",
                registrationChargeRepository.count());

        return counts;
    }
}