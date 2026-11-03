
package com.dap.calculation.admin.tax.taxconditionfield.service;

import com.dap.calculation.admin.tax.taxconditionfield.dto.TaxConditionFieldRequestDTO;
import com.dap.calculation.admin.tax.taxconditionfield.dto.TaxConditionFieldResponseDTO;

import com.dap.calculation.entity.TaxConditionField;
import com.dap.calculation.repository.TaxConditionFieldRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class AdminTaxConditionFieldService {

    private final TaxConditionFieldRepository repository;

    public AdminTaxConditionFieldService(
            TaxConditionFieldRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TaxConditionFieldResponseDTO> getAllFields() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaxConditionFieldResponseDTO> getActiveFields() {
        return repository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaxConditionFieldResponseDTO getFieldById(Long id) {
        return mapToResponse(findField(id));
    }

    @Transactional
    public TaxConditionFieldResponseDTO createField(
            TaxConditionFieldRequestDTO request) {

        validateRequest(request);

        String fieldCode = request.getFieldCode().trim();

        if (repository.existsByFieldCode(fieldCode)) {
            throw new IllegalArgumentException(
                    "Condition field code already exists: " + fieldCode
            );
        }

        TaxConditionField field = new TaxConditionField();

        field.setFieldCode(fieldCode);
        field.setFieldLabel(request.getFieldLabel().trim());
        field.setDataType(normalize(request.getDataType()));
        field.setValueSource(normalize(request.getValueSource()));
        field.setDescription(normalizeDescription(request.getDescription()));

        field.setIsActive(
                request.getIsActive() == null || request.getIsActive()
        );

        return mapToResponse(repository.save(field));
    }

    @Transactional
    public TaxConditionFieldResponseDTO updateField(
            Long id,
            TaxConditionFieldRequestDTO request) {

        validateRequest(request);

        TaxConditionField existing = findField(id);

        String fieldCode = request.getFieldCode().trim();

        if (repository.existsByFieldCodeAndConditionFieldIdNot(
                fieldCode, id)) {
            throw new IllegalArgumentException(
                    "Condition field code already exists: " + fieldCode
            );
        }

        existing.setFieldCode(fieldCode);
        existing.setFieldLabel(request.getFieldLabel().trim());
        existing.setDataType(normalize(request.getDataType()));
        existing.setValueSource(normalize(request.getValueSource()));
        existing.setDescription(normalizeDescription(request.getDescription()));

        if (request.getIsActive() != null) {
            existing.setIsActive(request.getIsActive());
        }

        return mapToResponse(repository.save(existing));
    }

    @Transactional
    public TaxConditionFieldResponseDTO updateFieldStatus(
            Long id,
            Boolean active) {

        if (active == null) {
            throw new IllegalArgumentException(
                    "Active status is required."
            );
        }

        TaxConditionField field = findField(id);
        field.setIsActive(active);

        return mapToResponse(repository.save(field));
    }

    private void validateRequest(TaxConditionFieldRequestDTO request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Condition field request is required."
            );
        }

        if (isBlank(request.getFieldCode())) {
            throw new IllegalArgumentException(
                    "Field code is required."
            );
        }

        if (request.getFieldCode().trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Field code cannot exceed 100 characters."
            );
        }

        if (isBlank(request.getFieldLabel())) {
            throw new IllegalArgumentException(
                    "Field label is required."
            );
        }

        if (request.getFieldLabel().trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Field label cannot exceed 100 characters."
            );
        }

        String dataType = normalize(request.getDataType());

        if (!List.of("STRING", "NUMBER").contains(dataType)) {
            throw new IllegalArgumentException(
                    "Data type must be STRING or NUMBER."
            );
        }

        String valueSource = normalize(request.getValueSource());

        if (!List.of(
                "VEHICLE_DISTINCT_VALUES",
                "NUMERIC_INPUT"
        ).contains(valueSource)) {
            throw new IllegalArgumentException(
                    "Value source must be VEHICLE_DISTINCT_VALUES "
                            + "or NUMERIC_INPUT."
            );
        }

        if ("STRING".equals(dataType)
                && !"VEHICLE_DISTINCT_VALUES".equals(valueSource)) {
            throw new IllegalArgumentException(
                    "STRING fields must use VEHICLE_DISTINCT_VALUES."
            );
        }

        if ("NUMBER".equals(dataType)
                && !"NUMERIC_INPUT".equals(valueSource)) {
            throw new IllegalArgumentException(
                    "NUMBER fields must use NUMERIC_INPUT."
            );
        }

        if (request.getDescription() != null
                && request.getDescription().length() > 255) {
            throw new IllegalArgumentException(
                    "Description cannot exceed 255 characters."
            );
        }
    }

    private TaxConditionField findField(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Condition field not found: " + id
                ));
    }

    private TaxConditionFieldResponseDTO mapToResponse(
            TaxConditionField field) {

        TaxConditionFieldResponseDTO response =
                new TaxConditionFieldResponseDTO();

        response.setConditionFieldId(field.getConditionFieldId());
        response.setFieldCode(field.getFieldCode());
        response.setFieldLabel(field.getFieldLabel());
        response.setDataType(field.getDataType());
        response.setValueSource(field.getValueSource());
        response.setDescription(field.getDescription());
        response.setIsActive(field.getIsActive());

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
