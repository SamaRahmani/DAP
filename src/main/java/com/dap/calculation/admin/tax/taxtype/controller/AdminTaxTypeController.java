package com.dap.calculation.admin.tax.taxtype.controller;

import com.dap.calculation.admin.tax.taxtype.dto.AdminTaxTypeRequestDTO;
import com.dap.calculation.admin.tax.taxtype.dto.AdminTaxTypeResponseDTO;
import com.dap.calculation.admin.tax.taxtype.service.AdminTaxTypeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/tax-types")
public class AdminTaxTypeController {

    private final AdminTaxTypeService adminTaxTypeService;

    public AdminTaxTypeController(
            AdminTaxTypeService adminTaxTypeService) {
        this.adminTaxTypeService = adminTaxTypeService;
    }

    // GET: Retrieve all tax types
    @GetMapping
    public ResponseEntity<List<AdminTaxTypeResponseDTO>> getAllTaxTypes() {

        return ResponseEntity.ok(
                adminTaxTypeService.getAllTaxTypes());
    }

    // GET: Retrieve a tax type by ID
    @GetMapping("/{taxTypeId}")
    public ResponseEntity<AdminTaxTypeResponseDTO> getTaxTypeById(
            @PathVariable Long taxTypeId) {

        return ResponseEntity.ok(
                adminTaxTypeService.getTaxTypeById(taxTypeId));
    }

    // POST: Create a tax type
    @PostMapping
    public ResponseEntity<AdminTaxTypeResponseDTO> createTaxType(
            @RequestBody AdminTaxTypeRequestDTO requestDTO) {

        AdminTaxTypeResponseDTO response =
                adminTaxTypeService.createTaxType(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PUT: Update a tax type
    @PutMapping("/{taxTypeId}")
    public ResponseEntity<AdminTaxTypeResponseDTO> updateTaxType(
            @PathVariable Long taxTypeId,
            @RequestBody AdminTaxTypeRequestDTO requestDTO) {

        return ResponseEntity.ok(
                adminTaxTypeService.updateTaxType(
                        taxTypeId, requestDTO));
    }

    // DELETE: Delete a tax type
    @DeleteMapping("/{taxTypeId}")
    public ResponseEntity<Void> deleteTaxType(
            @PathVariable Long taxTypeId) {

        adminTaxTypeService.deleteTaxType(taxTypeId);

        return ResponseEntity.noContent().build();
    }
}