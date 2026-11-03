
package com.dap.calculation.admin.tax.taxconditionfield.controller;

import com.dap.calculation.admin.tax.taxconditionfield.dto.TaxConditionFieldRequestDTO;
import com.dap.calculation.admin.tax.taxconditionfield.dto.TaxConditionFieldResponseDTO;
import com.dap.calculation.admin.tax.taxconditionfield.service.AdminTaxConditionFieldService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/tax-condition-fields")
public class AdminTaxConditionFieldController {

    private final AdminTaxConditionFieldService service;

    public AdminTaxConditionFieldController(
            AdminTaxConditionFieldService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TaxConditionFieldResponseDTO>> getAllFields() {
        return ResponseEntity.ok(service.getAllFields());
    }

    @GetMapping("/active")
    public ResponseEntity<List<TaxConditionFieldResponseDTO>> getActiveFields() {
        return ResponseEntity.ok(service.getActiveFields());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaxConditionFieldResponseDTO> getFieldById(
            @PathVariable Long id) {
        return ResponseEntity.ok(service.getFieldById(id));
    }

    @PostMapping
    public ResponseEntity<TaxConditionFieldResponseDTO> createField(
            @RequestBody TaxConditionFieldRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createField(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaxConditionFieldResponseDTO> updateField(
            @PathVariable Long id,
            @RequestBody TaxConditionFieldRequestDTO request) {

        return ResponseEntity.ok(service.updateField(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaxConditionFieldResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam Boolean active) {

        return ResponseEntity.ok(
                service.updateFieldStatus(id, active)
        );
    }
}
