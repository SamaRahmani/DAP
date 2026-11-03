
package com.dap.calculation.admin.tax.taxcalculationbase.service;

import com.dap.calculation.admin.tax.taxcalculationbase.dto.TaxCalculationBaseRequestDTO;
import com.dap.calculation.admin.tax.taxcalculationbase.dto.TaxCalculationBaseResponseDTO;

import com.dap.calculation.entity.TaxCalculationBase;
import com.dap.calculation.repository.TaxCalculationBaseRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class AdminTaxCalculationBaseService {

    private final TaxCalculationBaseRepository repository;

    public AdminTaxCalculationBaseService(
            TaxCalculationBaseRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TaxCalculationBaseResponseDTO> getAllBases() {

        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaxCalculationBaseResponseDTO> getActiveBases() {

        return repository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaxCalculationBaseResponseDTO getBaseById(Long id) {

        return mapToResponse(findBase(id));
    }

    @Transactional
    public TaxCalculationBaseResponseDTO createBase(
            TaxCalculationBaseRequestDTO request) {

        validateRequest(request);

        String code = normalize(request.getBaseCode());

        if (repository.existsByBaseCode(code)) {
            throw new IllegalArgumentException(
                    "Calculation base code already exists: " + code
            );
        }

        TaxCalculationBase base = new TaxCalculationBase();

        base.setBaseCode(code);
        base.setBaseName(request.getBaseName().trim());
        base.setDescription(normalizeDescription(request.getDescription()));

        base.setIsActive(
                request.getIsActive() == null || request.getIsActive()
        );

        return mapToResponse(repository.save(base));
    }

    @Transactional
    public TaxCalculationBaseResponseDTO updateBase(
            Long id,
            TaxCalculationBaseRequestDTO request) {

        validateRequest(request);

        TaxCalculationBase existing = findBase(id);

        String code = normalize(request.getBaseCode());

        if (repository.existsByBaseCodeAndCalculationBaseIdNot(
                code, id)) {

            throw new IllegalArgumentException(
                    "Calculation base code already exists: " + code
            );
        }

        existing.setBaseCode(code);
        existing.setBaseName(request.getBaseName().trim());
        existing.setDescription(normalizeDescription(request.getDescription()));

        if (request.getIsActive() != null) {
            existing.setIsActive(request.getIsActive());
        }

        return mapToResponse(repository.save(existing));
    }

    @Transactional
    public TaxCalculationBaseResponseDTO updateBaseStatus(
            Long id,
            Boolean active) {

        if (active == null) {
            throw new IllegalArgumentException(
                    "Active status is required."
            );
        }

        TaxCalculationBase base = findBase(id);

        base.setIsActive(active);

        return mapToResponse(repository.save(base));
    }

    private void validateRequest(TaxCalculationBaseRequestDTO request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Calculation base request is required."
            );
        }

        if (isBlank(request.getBaseCode())) {
            throw new IllegalArgumentException(
                    "Calculation base code is required."
            );
        }

        if (request.getBaseCode().trim().length() > 50) {
            throw new IllegalArgumentException(
                    "Calculation base code cannot exceed 50 characters."
            );
        }

        if (isBlank(request.getBaseName())) {
            throw new IllegalArgumentException(
                    "Calculation base name is required."
            );
        }

        if (request.getBaseName().trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Calculation base name cannot exceed 100 characters."
            );
        }

        if (request.getDescription() != null
                && request.getDescription().length() > 255) {
            throw new IllegalArgumentException(
                    "Description cannot exceed 255 characters."
            );
        }
    }

    private TaxCalculationBase findBase(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Calculation base not found: " + id
                        ));
    }

    private TaxCalculationBaseResponseDTO mapToResponse(
            TaxCalculationBase base) {

        TaxCalculationBaseResponseDTO response =
                new TaxCalculationBaseResponseDTO();

        response.setCalculationBaseId(base.getCalculationBaseId());
        response.setBaseCode(base.getBaseCode());
        response.setBaseName(base.getBaseName());
        response.setDescription(base.getDescription());
        response.setIsActive(base.getIsActive());

        return response;
    }

    private String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeDescription(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
