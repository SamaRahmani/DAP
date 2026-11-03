
package com.dap.calculation.admin.tax.taxcalculationbase.controller;

import com.dap.calculation.admin.tax.taxcalculationbase.dto.TaxCalculationBaseRequestDTO;
import com.dap.calculation.admin.tax.taxcalculationbase.dto.TaxCalculationBaseResponseDTO;
import com.dap.calculation.admin.tax.taxcalculationbase.service.AdminTaxCalculationBaseService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/tax-calculation-bases")
public class AdminTaxCalculationBaseController {

    private final AdminTaxCalculationBaseService service;

    public AdminTaxCalculationBaseController(
            AdminTaxCalculationBaseService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TaxCalculationBaseResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAllBases());
    }

    @GetMapping("/active")
    public ResponseEntity<List<TaxCalculationBaseResponseDTO>> getActive() {
        return ResponseEntity.ok(service.getActiveBases());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaxCalculationBaseResponseDTO> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(service.getBaseById(id));
    }

    @PostMapping
    public ResponseEntity<TaxCalculationBaseResponseDTO> create(
            @RequestBody TaxCalculationBaseRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createBase(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaxCalculationBaseResponseDTO> update(
            @PathVariable Long id,
            @RequestBody TaxCalculationBaseRequestDTO request) {

        return ResponseEntity.ok(service.updateBase(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaxCalculationBaseResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam Boolean active) {

        return ResponseEntity.ok(
                service.updateBaseStatus(id, active)
        );
    }
}
