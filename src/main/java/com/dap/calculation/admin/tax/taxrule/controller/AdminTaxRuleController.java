package com.dap.calculation.admin.tax.taxrule.controller;

import com.dap.calculation.admin.tax.taxrule.dto.AdminTaxRuleRequestDTO;
import com.dap.calculation.admin.tax.taxrule.dto.AdminTaxRuleResponseDTO;
import com.dap.calculation.admin.tax.taxrule.service.AdminTaxRuleService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/tax-rules")
public class AdminTaxRuleController {

    private final AdminTaxRuleService adminTaxRuleService;

    public AdminTaxRuleController(
            AdminTaxRuleService adminTaxRuleService) {
        this.adminTaxRuleService = adminTaxRuleService;
    }

    @GetMapping
    public ResponseEntity<List<AdminTaxRuleResponseDTO>> getAllTaxRules() {
        return ResponseEntity.ok(
                adminTaxRuleService.getAllTaxRules()
        );
    }

    @GetMapping("/{taxRuleId}")
    public ResponseEntity<AdminTaxRuleResponseDTO> getTaxRuleById(
            @PathVariable Long taxRuleId) {
        return ResponseEntity.ok(
                adminTaxRuleService.getTaxRuleById(taxRuleId)
        );
    }

    @PostMapping
    public ResponseEntity<AdminTaxRuleResponseDTO> createTaxRule(
            @RequestBody AdminTaxRuleRequestDTO requestDTO) {

        AdminTaxRuleResponseDTO response =
                adminTaxRuleService.createTaxRule(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{taxRuleId}")
    public ResponseEntity<AdminTaxRuleResponseDTO> updateTaxRule(
            @PathVariable Long taxRuleId,
            @RequestBody AdminTaxRuleRequestDTO requestDTO) {

        return ResponseEntity.ok(
                adminTaxRuleService.updateTaxRule(
                        taxRuleId, requestDTO
                )
        );
    }

    @DeleteMapping("/{taxRuleId}")
    public ResponseEntity<Void> deleteTaxRule(
            @PathVariable Long taxRuleId) {

        adminTaxRuleService.deleteTaxRule(taxRuleId);

        return ResponseEntity.noContent().build();
    }
}