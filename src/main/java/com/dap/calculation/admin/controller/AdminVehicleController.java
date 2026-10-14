package com.dap.calculation.admin.controller;

import com.dap.calculation.admin.dto.AdminVehicleRequestDTO;
import com.dap.calculation.admin.dto.AdminVehicleResponseDTO;
import com.dap.calculation.admin.service.AdminVehicleService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/vehicles")
public class AdminVehicleController {

    private final AdminVehicleService adminVehicleService;

    public AdminVehicleController(AdminVehicleService adminVehicleService) {
        this.adminVehicleService = adminVehicleService;
    }

    // GET: Retrieve all vehicles
    @GetMapping
    public ResponseEntity<List<AdminVehicleResponseDTO>> getAllVehicles() {

        List<AdminVehicleResponseDTO> vehicles =
                adminVehicleService.getAllVehicles();

        return ResponseEntity.ok(vehicles);
    }

    // GET: Retrieve a vehicle by ID
    @GetMapping("/{vehicleId}")
    public ResponseEntity<AdminVehicleResponseDTO> getVehicleById(
            @PathVariable String vehicleId) {

        AdminVehicleResponseDTO response =
                adminVehicleService.getVehicleById(vehicleId);

        return ResponseEntity.ok(response);
    }

    // POST: Create a new vehicle
    @PostMapping
    public ResponseEntity<AdminVehicleResponseDTO> createVehicle(
            @RequestBody AdminVehicleRequestDTO requestDTO) {

        AdminVehicleResponseDTO response =
                adminVehicleService.createVehicle(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PUT: Fully update an existing vehicle
    @PutMapping("/{vehicleId}")
    public ResponseEntity<AdminVehicleResponseDTO> updateVehicle(
            @PathVariable String vehicleId,
            @RequestBody AdminVehicleRequestDTO requestDTO) {

        AdminVehicleResponseDTO response =
                adminVehicleService.updateVehicle(vehicleId, requestDTO);

        return ResponseEntity.ok(response);
    }

    // PATCH: Partially update an existing vehicle
    @PatchMapping("/{vehicleId}")
    public ResponseEntity<AdminVehicleResponseDTO> patchVehicle(
            @PathVariable String vehicleId,
            @RequestBody AdminVehicleRequestDTO requestDTO) {

        AdminVehicleResponseDTO response =
                adminVehicleService.patchVehicle(vehicleId, requestDTO);

        return ResponseEntity.ok(response);
    }

    // DELETE: Delete a vehicle
    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(
            @PathVariable String vehicleId) {

        adminVehicleService.deleteVehicle(vehicleId);

        return ResponseEntity.noContent().build();
    }
}