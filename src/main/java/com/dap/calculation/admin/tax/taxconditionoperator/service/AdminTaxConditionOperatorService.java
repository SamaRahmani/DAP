
package com.dap.calculation.admin.tax.taxconditionoperator.service;

import com.dap.calculation.admin.tax.taxconditionoperator.dto.TaxConditionOperatorRequestDTO;
import com.dap.calculation.admin.tax.taxconditionoperator.dto.TaxConditionOperatorResponseDTO;

import com.dap.calculation.entity.TaxConditionField;
import com.dap.calculation.entity.TaxConditionFieldOperator;

import com.dap.calculation.repository.TaxConditionFieldRepository;
import com.dap.calculation.repository.TaxConditionFieldOperatorRepository;
import com.dap.calculation.repository.TaxRuleConditionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class AdminTaxConditionOperatorService {

    private final TaxConditionFieldRepository fieldRepository;
    private final TaxConditionFieldOperatorRepository operatorRepository;
    private final TaxRuleConditionRepository conditionRepository;

    public AdminTaxConditionOperatorService(
            TaxConditionFieldRepository fieldRepository,
            TaxConditionFieldOperatorRepository operatorRepository,
            TaxRuleConditionRepository conditionRepository) {

        this.fieldRepository = fieldRepository;
        this.operatorRepository = operatorRepository;
        this.conditionRepository = conditionRepository;
    }

    @Transactional(readOnly = true)
    public List<TaxConditionOperatorResponseDTO> getAllOperators() {

        return operatorRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaxConditionOperatorResponseDTO> getOperatorsByField(
            Long conditionFieldId) {

        findActiveField(conditionFieldId);

        return operatorRepository
                .findByConditionFieldId(conditionFieldId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaxConditionOperatorResponseDTO getOperatorById(Long id) {

        TaxConditionFieldOperator operator =
                operatorRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Condition operator not found: " + id
                                ));

        return mapToResponse(operator);
    }

    @Transactional
    public TaxConditionOperatorResponseDTO createOperator(
            TaxConditionOperatorRequestDTO request) {

        validateRequest(request);

        Long fieldId = request.getConditionFieldId();
        String operatorCode = normalize(request.getOperator());

        TaxConditionField field = findActiveField(fieldId);

        validateOperatorForField(field, operatorCode);

        if (operatorRepository.existsByConditionFieldIdAndOperator(
                fieldId, operatorCode)) {

            throw new IllegalArgumentException(
                    "Operator already exists for this condition field."
            );
        }

        TaxConditionFieldOperator operator =
                new TaxConditionFieldOperator();

        operator.setConditionFieldId(fieldId);
        operator.setOperator(operatorCode);

        return mapToResponse(operatorRepository.save(operator));
    }

    @Transactional
    public void deleteOperator(Long id) {

        TaxConditionFieldOperator operator =
                operatorRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Condition operator not found: " + id
                                ));

        boolean used = conditionRepository
                .existsByConditionFieldIdAndOperator(
                        operator.getConditionFieldId(),
                        operator.getOperator()
                );

        if (used) {
            throw new IllegalArgumentException(
                    "This operator is already used by a tax rule. "
                            + "It cannot be deleted."
            );
        }

        operatorRepository.delete(operator);
    }

    private void validateRequest(
            TaxConditionOperatorRequestDTO request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Operator request is required."
            );
        }

        if (request.getConditionFieldId() == null) {
            throw new IllegalArgumentException(
                    "Condition field ID is required."
            );
        }

        if (request.getOperator() == null
                || request.getOperator().isBlank()) {
            throw new IllegalArgumentException(
                    "Operator is required."
            );
        }
    }

    private TaxConditionField findActiveField(Long id) {

        return fieldRepository
                .findById(id)
                .filter(field -> Boolean.TRUE.equals(field.getIsActive()))
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Condition field not found or inactive: " + id
                        ));
    }

    private void validateOperatorForField(
            TaxConditionField field,
            String operator) {

        List<String> supportedOperators = List.of(
                "EQUALS",
                "NOT_EQUALS",
                "GREATER_THAN",
                "GREATER_THAN_OR_EQUAL",
                "LESS_THAN",
                "LESS_THAN_OR_EQUAL",
                "BETWEEN",
                "IN"
        );

        if (!supportedOperators.contains(operator)) {
            throw new IllegalArgumentException(
                    "Unsupported operator: " + operator
            );
        }

        if ("STRING".equalsIgnoreCase(field.getDataType())
                && List.of(
                "GREATER_THAN",
                "GREATER_THAN_OR_EQUAL",
                "LESS_THAN",
                "LESS_THAN_OR_EQUAL",
                "BETWEEN"
        ).contains(operator)) {

            throw new IllegalArgumentException(
                    "Numeric comparison operators cannot be used "
                            + "with STRING fields."
            );
        }
    }

    private TaxConditionOperatorResponseDTO mapToResponse(
            TaxConditionFieldOperator operator) {

        TaxConditionField field = fieldRepository
                .findById(operator.getConditionFieldId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Condition field not found: "
                                        + operator.getConditionFieldId()
                        ));

        TaxConditionOperatorResponseDTO response =
                new TaxConditionOperatorResponseDTO();

        response.setConditionFieldOperatorId(
                operator.getConditionFieldOperatorId()
        );
        response.setConditionFieldId(
                operator.getConditionFieldId()
        );
        response.setFieldCode(field.getFieldCode());
        response.setFieldLabel(field.getFieldLabel());
        response.setOperator(operator.getOperator());

        return response;
    }

    private String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }
}
