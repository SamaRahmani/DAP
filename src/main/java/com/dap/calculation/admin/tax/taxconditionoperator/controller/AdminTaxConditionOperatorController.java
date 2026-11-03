
package com.dap.calculation.admin.tax.taxconditionoperator.controller;

import com.dap.calculation.admin.tax.taxconditionoperator.dto.TaxConditionOperatorRequestDTO;
import com.dap.calculation.admin.tax.taxconditionoperator.dto.TaxConditionOperatorResponseDTO;
import com.dap.calculation.admin.tax.taxconditionoperator.service.AdminTaxConditionOperatorService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/tax-condition-operators")
public class AdminTaxConditionOperatorController {

    private final AdminTaxConditionOperatorService service;

    public AdminTaxConditionOperatorController(
            AdminTaxConditionOperatorService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TaxConditionOperatorResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAllOperators());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaxConditionOperatorResponseDTO> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(service.getOperatorById(id));
    }

    @GetMapping("/field/{conditionFieldId}")
    public ResponseEntity<List<TaxConditionOperatorResponseDTO>> getByField(
            @PathVariable Long conditionFieldId) {
        return ResponseEntity.ok(
                service.getOperatorsByField(conditionFieldId)
        );
    }

    @PostMapping
    public ResponseEntity<TaxConditionOperatorResponseDTO> create(
            @RequestBody TaxConditionOperatorRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createOperator(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteOperator(id);
        return ResponseEntity.noContent().build();
    }
}
