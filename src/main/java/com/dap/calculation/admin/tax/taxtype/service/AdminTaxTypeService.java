package com.dap.calculation.admin.tax.taxtype.service;

import com.dap.calculation.admin.tax.taxtype.dto.AdminTaxTypeRequestDTO;
import com.dap.calculation.admin.tax.taxtype.dto.AdminTaxTypeResponseDTO;
import com.dap.calculation.entity.TaxType;
import com.dap.calculation.repository.TaxTypeRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminTaxTypeService {

    private final TaxTypeRepository taxTypeRepository;

    public AdminTaxTypeService(
            TaxTypeRepository taxTypeRepository) {
        this.taxTypeRepository = taxTypeRepository;
    }

    // Get all tax types
    public List<AdminTaxTypeResponseDTO> getAllTaxTypes() {
        return taxTypeRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // Get a tax type by ID
    public AdminTaxTypeResponseDTO getTaxTypeById(Long taxTypeId) {
        TaxType taxType = taxTypeRepository.findById(taxTypeId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Tax type not found with ID: " + taxTypeId));

        return mapToResponseDTO(taxType);
    }

    // Create a tax type
    public AdminTaxTypeResponseDTO createTaxType(
            AdminTaxTypeRequestDTO requestDTO) {

        validateRequest(requestDTO);

        String taxCode = requestDTO.getTaxCode().trim();

        if (taxTypeRepository.existsByTaxCode(taxCode)) {
            throw new RuntimeException(
                    "Tax code already exists: " + taxCode);
        }

        TaxType taxType = mapToEntity(requestDTO);

        TaxType savedTaxType = taxTypeRepository.save(taxType);

        return mapToResponseDTO(savedTaxType);
    }

    // Update a tax type
    public AdminTaxTypeResponseDTO updateTaxType(
            Long taxTypeId,
            AdminTaxTypeRequestDTO requestDTO) {

        validateRequest(requestDTO);

        TaxType existingTaxType = taxTypeRepository.findById(taxTypeId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Tax type not found with ID: " + taxTypeId));

        String taxCode = requestDTO.getTaxCode().trim();

        boolean codeBelongsToAnotherTaxType =
                taxTypeRepository.findByTaxCode(taxCode)
                        .filter(existing -> !existing.getTaxTypeId()
                                .equals(taxTypeId))
                        .isPresent();

        if (codeBelongsToAnotherTaxType) {
            throw new RuntimeException(
                    "Tax code already exists: " + taxCode);
        }

        existingTaxType.setTaxCode(taxCode);
        existingTaxType.setTaxName(requestDTO.getTaxName().trim());
        existingTaxType.setDescription(
                normalizeDescription(requestDTO.getDescription()));

        TaxType updatedTaxType =
                taxTypeRepository.save(existingTaxType);

        return mapToResponseDTO(updatedTaxType);
    }

    // Delete a tax type
    public void deleteTaxType(Long taxTypeId) {

        TaxType existingTaxType = taxTypeRepository.findById(taxTypeId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Tax type not found with ID: " + taxTypeId));

        // The database foreign key restricts deletion when rules reference it.
        taxTypeRepository.delete(existingTaxType);
    }

    private void validateRequest(AdminTaxTypeRequestDTO requestDTO) {

        if (requestDTO == null) {
            throw new IllegalArgumentException(
                    "Tax type request is required.");
        }

        if (requestDTO.getTaxCode() == null
                || requestDTO.getTaxCode().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Tax code is required.");
        }

        if (requestDTO.getTaxName() == null
                || requestDTO.getTaxName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Tax name is required.");
        }

        if (requestDTO.getTaxCode().trim().length() > 30) {
            throw new IllegalArgumentException(
                    "Tax code cannot exceed 30 characters.");
        }

        if (requestDTO.getTaxName().trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Tax name cannot exceed 100 characters.");
        }

        if (requestDTO.getDescription() != null
                && requestDTO.getDescription().length() > 255) {
            throw new IllegalArgumentException(
                    "Description cannot exceed 255 characters.");
        }
    }

    private TaxType mapToEntity(
            AdminTaxTypeRequestDTO requestDTO) {

        TaxType taxType = new TaxType();

        taxType.setTaxCode(requestDTO.getTaxCode().trim());
        taxType.setTaxName(requestDTO.getTaxName().trim());
        taxType.setDescription(
                normalizeDescription(requestDTO.getDescription()));

        return taxType;
    }

    private AdminTaxTypeResponseDTO mapToResponseDTO(
            TaxType taxType) {

        AdminTaxTypeResponseDTO responseDTO =
                new AdminTaxTypeResponseDTO();

        responseDTO.setTaxTypeId(taxType.getTaxTypeId());
        responseDTO.setTaxCode(taxType.getTaxCode());
        responseDTO.setTaxName(taxType.getTaxName());
        responseDTO.setDescription(taxType.getDescription());

        return responseDTO;
    }

    private String normalizeDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            return null;
        }

        return description.trim();
    }
}