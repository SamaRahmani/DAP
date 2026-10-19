package com.dap.calculation.admin.registration.controller;

import com.dap.calculation.admin.registration.dto.AdminRegistrationChargeRequestDTO;
import com.dap.calculation.admin.registration.dto.AdminRegistrationChargeResponseDTO;
import com.dap.calculation.admin.registration.service.AdminRegistrationChargeService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/registration-charges")
public class AdminRegistrationChargeController {

    private final AdminRegistrationChargeService adminRegistrationChargeService;

    public AdminRegistrationChargeController(
            AdminRegistrationChargeService adminRegistrationChargeService) {

        this.adminRegistrationChargeService = adminRegistrationChargeService;
    }

    @GetMapping
    public List<AdminRegistrationChargeResponseDTO> getAllRegistrationCharges() {
        return adminRegistrationChargeService.getAllRegistrationCharges();
    }

    @GetMapping("/{registrationChargeId}")
    public AdminRegistrationChargeResponseDTO getRegistrationChargeById(
            @PathVariable Long registrationChargeId) {

        return adminRegistrationChargeService
                .getRegistrationChargeById(registrationChargeId);
    }

    @GetMapping("/state/{stateCode}")
    public List<AdminRegistrationChargeResponseDTO> getRegistrationChargesByState(
            @PathVariable String stateCode) {

        return adminRegistrationChargeService
                .getRegistrationChargesByState(stateCode);
    }

    @GetMapping("/state/{stateCode}/state-level")
    public List<AdminRegistrationChargeResponseDTO> getStateLevelCharges(
            @PathVariable String stateCode) {

        return adminRegistrationChargeService
                .getStateLevelCharges(stateCode);
    }

    @GetMapping("/city/{cityId}")
    public List<AdminRegistrationChargeResponseDTO> getCityLevelCharges(
            @PathVariable Long cityId) {

        return adminRegistrationChargeService
                .getCityLevelCharges(cityId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminRegistrationChargeResponseDTO createRegistrationCharge(
            @RequestBody AdminRegistrationChargeRequestDTO request) {

        return adminRegistrationChargeService
                .createRegistrationCharge(request);
    }

    @PutMapping("/{registrationChargeId}")
    public AdminRegistrationChargeResponseDTO updateRegistrationCharge(
            @PathVariable Long registrationChargeId,
            @RequestBody AdminRegistrationChargeRequestDTO request) {

        return adminRegistrationChargeService
                .updateRegistrationCharge(registrationChargeId, request);
    }

    @DeleteMapping("/{registrationChargeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRegistrationCharge(
            @PathVariable Long registrationChargeId) {

        adminRegistrationChargeService
                .deleteRegistrationCharge(registrationChargeId);
    }
}