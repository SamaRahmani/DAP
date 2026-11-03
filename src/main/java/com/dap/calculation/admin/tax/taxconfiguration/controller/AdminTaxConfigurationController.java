
package com.dap.calculation.admin.tax.taxconfiguration.controller;

import com.dap.calculation.admin.tax.taxconfiguration.dto.TaxConfigurationResponseDTO;
import com.dap.calculation.admin.tax.taxconfiguration.service.AdminTaxConfigurationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/tax-configuration")
public class AdminTaxConfigurationController {

    private final AdminTaxConfigurationService service;

    public AdminTaxConfigurationController(
            AdminTaxConfigurationService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<TaxConfigurationResponseDTO> getConfiguration() {

        return ResponseEntity.ok(
                service.getTaxConfiguration()
        );
    }
}
