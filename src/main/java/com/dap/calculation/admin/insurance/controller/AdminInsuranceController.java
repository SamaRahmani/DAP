package com.dap.calculation.admin.insurance.controller;

import com.dap.calculation.admin.insurance.dto.AdminInsuranceRequestDTO;
import com.dap.calculation.admin.insurance.dto.AdminInsuranceResponseDTO;
import com.dap.calculation.admin.insurance.service.AdminInsuranceService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/insurance")
public class AdminInsuranceController {

    private final AdminInsuranceService adminInsuranceService;

    public AdminInsuranceController(
            AdminInsuranceService adminInsuranceService) {

        this.adminInsuranceService = adminInsuranceService;
    }

    // GET: All providers
    @GetMapping
    public List<AdminInsuranceResponseDTO> getAllInsuranceProviders() {

        return adminInsuranceService.getAllInsuranceProviders();
    }

    // GET: Provider by ID
    @GetMapping("/{insuranceId}")
    public AdminInsuranceResponseDTO getInsuranceById(
            @PathVariable String insuranceId) {

        return adminInsuranceService.getInsuranceById(insuranceId);
    }

    // POST: Create provider
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminInsuranceResponseDTO createInsurance(
            @RequestBody AdminInsuranceRequestDTO request) {

        return adminInsuranceService.createInsurance(request);
    }

    // PUT: Update provider
    @PutMapping("/{insuranceId}")
    public AdminInsuranceResponseDTO updateInsurance(
            @PathVariable String insuranceId,
            @RequestBody AdminInsuranceRequestDTO request) {

        return adminInsuranceService.updateInsurance(insuranceId, request);
    }

    // DELETE: Provider
    @DeleteMapping("/{insuranceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInsurance(@PathVariable String insuranceId) {

        adminInsuranceService.deleteInsurance(insuranceId);
    }
}