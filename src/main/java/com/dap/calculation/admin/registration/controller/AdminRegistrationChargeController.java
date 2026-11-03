
package com.dap.calculation.admin.registration.controller;

import com.dap.calculation.admin.registration.dto.AdminRegistrationChargeRequestDTO;
import com.dap.calculation.admin.registration.dto.AdminRegistrationChargeResponseDTO;
import com.dap.calculation.admin.registration.service.AdminRegistrationChargeService;

import jakarta.validation.Valid;

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

    // =====================================
    // GET: ALL REGISTRATION CHARGES
    // =====================================

    @GetMapping
    public List<AdminRegistrationChargeResponseDTO> getAllRegistrationCharges() {

        return adminRegistrationChargeService.getAllRegistrationCharges();
    }

    // =====================================
    // GET: REGISTRATION CHARGE BY ID
    // =====================================

    @GetMapping("/{registrationChargeId}")
    public AdminRegistrationChargeResponseDTO getRegistrationChargeById(
            @PathVariable Long registrationChargeId) {

        return adminRegistrationChargeService
                .getRegistrationChargeById(registrationChargeId);
    }

    // =====================================
    // GET: ALL CHARGES FOR A STATE
    // Includes NATIONAL and STATE/CITY charges
    // =====================================

    @GetMapping("/state/{stateCode}")
    public List<AdminRegistrationChargeResponseDTO> getRegistrationChargesByState(
            @PathVariable String stateCode) {

        return adminRegistrationChargeService
                .getRegistrationChargesByState(stateCode);
    }

    // =====================================
    // GET: STATE-LEVEL CHARGES
    // =====================================

    @GetMapping("/state/{stateCode}/state-level")
    public List<AdminRegistrationChargeResponseDTO> getStateLevelCharges(
            @PathVariable String stateCode) {

        return adminRegistrationChargeService
                .getStateLevelCharges(stateCode);
    }

    // =====================================
    // GET: CITY-LEVEL CHARGES
    // =====================================

    @GetMapping("/city/{cityId}")
    public List<AdminRegistrationChargeResponseDTO> getCityLevelCharges(
            @PathVariable Long cityId) {

        return adminRegistrationChargeService
                .getCityLevelCharges(cityId);
    }

    // =====================================
    // POST: CREATE REGISTRATION CHARGE
    // =====================================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminRegistrationChargeResponseDTO createRegistrationCharge(
            @Valid @RequestBody AdminRegistrationChargeRequestDTO request) {

        return adminRegistrationChargeService
                .createRegistrationCharge(request);
    }

    // =====================================
    // PUT: UPDATE REGISTRATION CHARGE
    // =====================================

    @PutMapping("/{registrationChargeId}")
    public AdminRegistrationChargeResponseDTO updateRegistrationCharge(
            @PathVariable Long registrationChargeId,
            @Valid @RequestBody AdminRegistrationChargeRequestDTO request) {

        return adminRegistrationChargeService
                .updateRegistrationCharge(registrationChargeId, request);
    }

    // =====================================
    // DELETE: REGISTRATION CHARGE
    // =====================================

    @DeleteMapping("/{registrationChargeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRegistrationCharge(
            @PathVariable Long registrationChargeId) {

        adminRegistrationChargeService
                .deleteRegistrationCharge(registrationChargeId);
    }
}
