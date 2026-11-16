package com.dap.calculation.controller;

import com.dap.calculation.entity.Insurance;
import com.dap.calculation.repository.InsuranceRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/insurance")
public class InsuranceController {

    private final InsuranceRepository insuranceRepository;

    public InsuranceController(
            InsuranceRepository insuranceRepository) {

        this.insuranceRepository = insuranceRepository;
    }

    // Get all insurance providers
    @GetMapping("/providers")
    public List<Insurance> getAllInsuranceProviders() {
        return insuranceRepository.findAll();
    }
}