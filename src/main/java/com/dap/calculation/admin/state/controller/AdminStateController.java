package com.dap.calculation.admin.state.controller;

import com.dap.calculation.admin.state.dto.AdminStateRequestDTO;
import com.dap.calculation.admin.state.dto.AdminStateResponseDTO;
import com.dap.calculation.admin.state.service.AdminStateService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/states")
public class AdminStateController {

    private final AdminStateService adminStateService;

    public AdminStateController(
            AdminStateService adminStateService
    ) {
        this.adminStateService = adminStateService;
    }

    @GetMapping
    public List<AdminStateResponseDTO> getAllStates() {
        return adminStateService.getAllStates();
    }

    @GetMapping("/{stateCode}")
    public AdminStateResponseDTO getStateByCode(
            @PathVariable String stateCode
    ) {
        return adminStateService.getStateByCode(stateCode);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminStateResponseDTO createState(
            @RequestBody AdminStateRequestDTO request
    ) {
        return adminStateService.createState(request);
    }

    @PutMapping("/{stateCode}")
    public AdminStateResponseDTO updateState(
            @PathVariable String stateCode,
            @RequestBody AdminStateRequestDTO request
    ) {
        return adminStateService.updateState(stateCode, request);
    }

    @DeleteMapping("/{stateCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteState(
            @PathVariable String stateCode
    ) {
        adminStateService.deleteState(stateCode);
    }
}