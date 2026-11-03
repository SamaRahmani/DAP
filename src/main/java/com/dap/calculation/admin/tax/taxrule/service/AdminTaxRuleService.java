
package com.dap.calculation.admin.tax.taxrule.service;

import com.dap.calculation.admin.tax.taxrule.dto.AdminTaxRuleRequestDTO;
import com.dap.calculation.admin.tax.taxrule.dto.AdminTaxRuleResponseDTO;

import com.dap.calculation.entity.TaxRule;
import com.dap.calculation.entity.TaxRuleApplicability;
import com.dap.calculation.entity.TaxRuleCondition;
import com.dap.calculation.entity.TaxType;
import com.dap.calculation.entity.TaxConditionField;
import com.dap.calculation.entity.TaxCalculationBase;

import com.dap.calculation.repository.TaxRuleRepository;
import com.dap.calculation.repository.TaxTypeRepository;
import com.dap.calculation.repository.TaxRuleConditionRepository;
import com.dap.calculation.repository.TaxRuleApplicabilityRepository;
import com.dap.calculation.repository.TaxConditionFieldRepository;
import com.dap.calculation.repository.TaxConditionFieldOperatorRepository;
import com.dap.calculation.repository.TaxCalculationBaseRepository;

import com.dap.calculation.repository.StateRepository;
import com.dap.calculation.repository.CityRepository;
import com.dap.calculation.repository.RtoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
public class AdminTaxRuleService {

    private final TaxRuleRepository taxRuleRepository;
    private final TaxTypeRepository taxTypeRepository;
    private final TaxRuleConditionRepository conditionRepository;
    private final TaxRuleApplicabilityRepository applicabilityRepository;

    private final TaxConditionFieldRepository taxConditionFieldRepository;
    private final TaxConditionFieldOperatorRepository taxConditionFieldOperatorRepository;
    private final TaxCalculationBaseRepository taxCalculationBaseRepository;

    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final RtoRepository rtoRepository;

    public AdminTaxRuleService(
            TaxRuleRepository taxRuleRepository,
            TaxTypeRepository taxTypeRepository,
            TaxRuleConditionRepository conditionRepository,
            TaxRuleApplicabilityRepository applicabilityRepository,
            TaxConditionFieldRepository taxConditionFieldRepository,
            TaxConditionFieldOperatorRepository taxConditionFieldOperatorRepository,
            TaxCalculationBaseRepository taxCalculationBaseRepository,
            StateRepository stateRepository,
            CityRepository cityRepository,
            RtoRepository rtoRepository) {

        this.taxRuleRepository = taxRuleRepository;
        this.taxTypeRepository = taxTypeRepository;
        this.conditionRepository = conditionRepository;
        this.applicabilityRepository = applicabilityRepository;

        this.taxConditionFieldRepository = taxConditionFieldRepository;
        this.taxConditionFieldOperatorRepository =
                taxConditionFieldOperatorRepository;
        this.taxCalculationBaseRepository = taxCalculationBaseRepository;

        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
        this.rtoRepository = rtoRepository;
    }

    // ---------------------------------------------------------
    // GET ALL TAX RULES
    // ---------------------------------------------------------

    @Transactional(readOnly = true)
    public List<AdminTaxRuleResponseDTO> getAllTaxRules() {

        return taxRuleRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // ---------------------------------------------------------
    // GET TAX RULE BY ID
    // ---------------------------------------------------------

    @Transactional(readOnly = true)
    public AdminTaxRuleResponseDTO getTaxRuleById(Long taxRuleId) {

        return mapToResponseDTO(findTaxRule(taxRuleId));
    }

    // ---------------------------------------------------------
    // CREATE TAX RULE
    // ---------------------------------------------------------

    @Transactional
    public AdminTaxRuleResponseDTO createTaxRule(
            AdminTaxRuleRequestDTO requestDTO) {

        validateRequest(requestDTO);
        validateTaxType(requestDTO.getTaxTypeId());

        String calculationType = normalize(
                requestDTO.getCalculationType()
        );

        BigDecimal taxValue = resolveTaxValue(requestDTO);

        TaxRule taxRule = new TaxRule();

        taxRule.setTaxTypeId(requestDTO.getTaxTypeId());
        taxRule.setRuleName(requestDTO.getRuleName().trim());

        taxRule.setCalculationType(calculationType);
        taxRule.setTaxValue(taxValue);

        taxRule.setCalculationBaseId(
                requestDTO.getCalculationBaseId()
        );

        // Compatibility with the old NOT NULL tax_percentage column.
        taxRule.setTaxPercentage(
                "PERCENTAGE".equals(calculationType)
                        ? taxValue
                        : BigDecimal.ZERO
        );

        taxRule.setPriority(
                requestDTO.getPriority() == null
                        ? 100
                        : requestDTO.getPriority()
        );

        taxRule.setIsFallback(
                Boolean.TRUE.equals(requestDTO.getIsFallback())
        );

        taxRule.setIsActive(
                requestDTO.getIsActive() == null
                        || requestDTO.getIsActive()
        );

        TaxRule savedRule = taxRuleRepository.save(taxRule);

        saveConditions(savedRule.getTaxRuleId(), requestDTO);
        saveApplicability(savedRule.getTaxRuleId(), requestDTO);

        return mapToResponseDTO(savedRule);
    }

    // ---------------------------------------------------------
    // UPDATE TAX RULE
    // ---------------------------------------------------------

    @Transactional
    public AdminTaxRuleResponseDTO updateTaxRule(
            Long taxRuleId,
            AdminTaxRuleRequestDTO requestDTO) {

        validateRequest(requestDTO);
        validateTaxType(requestDTO.getTaxTypeId());

        TaxRule existingRule = findTaxRule(taxRuleId);

        String calculationType = normalize(
                requestDTO.getCalculationType()
        );

        BigDecimal taxValue = resolveTaxValue(requestDTO);

        existingRule.setTaxTypeId(requestDTO.getTaxTypeId());
        existingRule.setRuleName(requestDTO.getRuleName().trim());

        existingRule.setCalculationType(calculationType);
        existingRule.setTaxValue(taxValue);

        existingRule.setCalculationBaseId(
                requestDTO.getCalculationBaseId()
        );

        existingRule.setTaxPercentage(
                "PERCENTAGE".equals(calculationType)
                        ? taxValue
                        : BigDecimal.ZERO
        );

        existingRule.setPriority(
                requestDTO.getPriority() == null
                        ? 100
                        : requestDTO.getPriority()
        );

        existingRule.setIsFallback(
                Boolean.TRUE.equals(requestDTO.getIsFallback())
        );

        if (requestDTO.getIsActive() != null) {
            existingRule.setIsActive(requestDTO.getIsActive());
        }

        TaxRule updatedRule = taxRuleRepository.save(existingRule);

        conditionRepository.deleteByTaxRuleId(taxRuleId);
        applicabilityRepository.deleteByTaxRuleId(taxRuleId);

        saveConditions(taxRuleId, requestDTO);
        saveApplicability(taxRuleId, requestDTO);

        return mapToResponseDTO(updatedRule);
    }

    // ---------------------------------------------------------
    // DELETE TAX RULE
    // ---------------------------------------------------------

    @Transactional
    public void deleteTaxRule(Long taxRuleId) {

        TaxRule existingRule = findTaxRule(taxRuleId);

        conditionRepository.deleteByTaxRuleId(taxRuleId);
        applicabilityRepository.deleteByTaxRuleId(taxRuleId);

        taxRuleRepository.delete(existingRule);
    }

    // ---------------------------------------------------------
    // FIND TAX RULE
    // ---------------------------------------------------------

    private TaxRule findTaxRule(Long taxRuleId) {

        return taxRuleRepository.findById(taxRuleId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Tax rule not found with ID: " + taxRuleId
                        ));
    }

    // ---------------------------------------------------------
    // VALIDATE TAX TYPE
    // ---------------------------------------------------------

    private void validateTaxType(Long taxTypeId) {

        if (taxTypeId == null
                || !taxTypeRepository.existsById(taxTypeId)) {

            throw new IllegalArgumentException(
                    "Valid taxTypeId is required."
            );
        }
    }

    // ---------------------------------------------------------
    // VALIDATE COMPLETE REQUEST
    // ---------------------------------------------------------

    private void validateRequest(AdminTaxRuleRequestDTO requestDTO) {

        if (requestDTO == null) {
            throw new IllegalArgumentException(
                    "Tax rule request is required."
            );
        }

        if (requestDTO.getTaxTypeId() == null) {
            throw new IllegalArgumentException(
                    "Tax type is required."
            );
        }

        if (requestDTO.getRuleName() == null
                || requestDTO.getRuleName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Rule name is required."
            );
        }

        if (requestDTO.getRuleName().trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Rule name cannot exceed 100 characters."
            );
        }

        validateCalculation(requestDTO);

        if (requestDTO.getPriority() != null
                && requestDTO.getPriority() < 1) {

            throw new IllegalArgumentException(
                    "Priority must be greater than zero."
            );
        }

        // Conditions are optional.
        if (requestDTO.getConditions() != null) {

            for (AdminTaxRuleRequestDTO.ConditionDTO condition
                    : requestDTO.getConditions()) {

                validateCondition(condition);
            }
        }

        if (requestDTO.getApplicability() == null
                || requestDTO.getApplicability().isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one applicability record is required."
            );
        }

        // One rule can have multiple geographical entries,
        // but all must belong to the same geographical level.
        String commonType = null;

        for (AdminTaxRuleRequestDTO.ApplicabilityDTO item
                : requestDTO.getApplicability()) {

            validateApplicability(item);

            String currentType = normalize(
                    item.getApplicabilityType()
            );

            if (commonType == null) {
                commonType = currentType;
            } else if (!commonType.equals(currentType)) {
                throw new IllegalArgumentException(
                        "A tax rule cannot mix geographical levels. "
                                + "Create separate rules for NATIONAL, "
                                + "STATE, CITY or RTO."
                );
            }
        }
    }

    // ---------------------------------------------------------
    // CALCULATION VALIDATION
    // ---------------------------------------------------------

    private void validateCalculation(
            AdminTaxRuleRequestDTO requestDTO) {

        String calculationType = normalize(
                requestDTO.getCalculationType()
        );

        if (calculationType == null) {
            calculationType = "PERCENTAGE";
        }

        if (!List.of("PERCENTAGE", "FIXED_AMOUNT")
                .contains(calculationType)) {

            throw new IllegalArgumentException(
                    "Calculation type must be PERCENTAGE or FIXED_AMOUNT."
            );
        }

        BigDecimal taxValue = resolveTaxValue(requestDTO);

        if (taxValue == null
                || taxValue.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Tax value must be zero or greater."
            );
        }

        if ("PERCENTAGE".equals(calculationType)) {

            if (taxValue.compareTo(new BigDecimal("100")) > 0) {
                throw new IllegalArgumentException(
                        "Percentage tax value cannot exceed 100."
                );
            }

            if (requestDTO.getCalculationBaseId() == null) {
                throw new IllegalArgumentException(
                        "Calculation base is required for percentage tax."
                );
            }

            taxCalculationBaseRepository
                    .findByCalculationBaseIdAndIsActiveTrue(
                            requestDTO.getCalculationBaseId()
                    )
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Calculation base does not exist "
                                            + "or is inactive."
                            ));

        } else {

            if (requestDTO.getCalculationBaseId() != null) {
                throw new IllegalArgumentException(
                        "Calculation base must be null for FIXED_AMOUNT."
                );
            }
        }
    }

    private BigDecimal resolveTaxValue(
            AdminTaxRuleRequestDTO requestDTO) {

        if (requestDTO.getTaxValue() != null) {
            return requestDTO.getTaxValue();
        }

        // Backward compatibility with existing clients.
        return requestDTO.getTaxPercentage();
    }

    // ---------------------------------------------------------
    // CONDITION VALIDATION
    // ---------------------------------------------------------

    private TaxConditionField validateCondition(
            AdminTaxRuleRequestDTO.ConditionDTO condition) {

        if (condition == null
                || condition.getFieldName() == null
                || condition.getFieldName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Condition field name is required."
            );
        }

        TaxConditionField field = taxConditionFieldRepository
                .findByFieldCodeAndIsActiveTrue(
                        condition.getFieldName().trim()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid or inactive condition field: "
                                        + condition.getFieldName()
                        ));

        if (condition.getOperator() == null
                || condition.getOperator().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Condition operator is required."
            );
        }

        String operator = normalize(condition.getOperator());

        boolean operatorAllowed =
                taxConditionFieldOperatorRepository
                        .existsByConditionFieldIdAndOperator(
                                field.getConditionFieldId(),
                                operator
                        );

        if (!operatorAllowed) {
            throw new IllegalArgumentException(
                    "Operator " + operator
                            + " is not supported for field "
                            + field.getFieldLabel()
            );
        }

        String valueFrom = condition.getValueFrom();
        String valueTo = condition.getValueTo();

        if (valueFrom == null || valueFrom.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Condition valueFrom is required."
            );
        }

        if ("BETWEEN".equals(operator)) {

            if (valueTo == null || valueTo.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "BETWEEN requires valueTo."
                );
            }

            if (!"NUMBER".equalsIgnoreCase(field.getDataType())) {
                throw new IllegalArgumentException(
                        "BETWEEN is supported only for numeric fields."
                );
            }

            try {
                BigDecimal from = new BigDecimal(valueFrom.trim());
                BigDecimal to = new BigDecimal(valueTo.trim());

                if (from.compareTo(to) > 0) {
                    throw new IllegalArgumentException(
                            "BETWEEN valueFrom must not exceed valueTo."
                    );
                }

            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException(
                        "BETWEEN requires numeric values."
                );
            }

        } else if (valueTo != null) {

            throw new IllegalArgumentException(
                    "valueTo must be null unless operator is BETWEEN."
            );
        }

        if ("NUMBER".equalsIgnoreCase(field.getDataType())) {

            try {
                new BigDecimal(valueFrom.trim());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException(
                        "Numeric condition fields require numeric values."
                );
            }
        }

        return field;
    }

    // ---------------------------------------------------------
    // GEOGRAPHICAL VALIDATION
    // ---------------------------------------------------------

    private void validateApplicability(
            AdminTaxRuleRequestDTO.ApplicabilityDTO applicability) {

        if (applicability == null
                || applicability.getApplicabilityType() == null
                || applicability.getApplicabilityCode() == null
                || applicability.getApplicabilityCode().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Applicability type and code are required."
            );
        }

        String type = normalize(
                applicability.getApplicabilityType()
        );

        String code = applicability.getApplicabilityCode().trim();

        switch (type) {

            case "NATIONAL":

                if (!List.of("ALL", "INDIA")
                        .contains(code.toUpperCase(Locale.ROOT))) {

                    throw new IllegalArgumentException(
                            "National applicability code must be ALL or INDIA."
                    );
                }
                break;

            case "STATE":

                if (!stateRepository.existsById(code)) {
                    throw new IllegalArgumentException(
                            "State does not exist: " + code
                    );
                }
                break;

            case "CITY":

                try {
                    Long cityId = Long.valueOf(code);

                    if (!cityRepository.existsById(cityId)) {
                        throw new IllegalArgumentException(
                                "City does not exist: " + code
                        );
                    }

                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException(
                            "City applicability code must be a valid city ID."
                    );
                }
                break;

            case "RTO":

                if (!rtoRepository.existsById(code)) {
                    throw new IllegalArgumentException(
                            "RTO does not exist: " + code
                    );
                }
                break;

            default:
                throw new IllegalArgumentException(
                        "Applicability type must be NATIONAL, STATE, CITY, or RTO."
                );
        }
    }

    // ---------------------------------------------------------
    // SAVE CONDITIONS
    // ---------------------------------------------------------

    private void saveConditions(
            Long taxRuleId,
            AdminTaxRuleRequestDTO requestDTO) {

        if (requestDTO.getConditions() == null) {
            return;
        }

        for (AdminTaxRuleRequestDTO.ConditionDTO conditionDTO
                : requestDTO.getConditions()) {

            TaxConditionField field = validateCondition(conditionDTO);

            TaxRuleCondition condition = new TaxRuleCondition();

            condition.setTaxRuleId(taxRuleId);
            condition.setConditionFieldId(
                    field.getConditionFieldId()
            );
            condition.setFieldName(field.getFieldCode());

            condition.setOperator(
                    normalize(conditionDTO.getOperator())
            );

            condition.setValueFrom(
                    conditionDTO.getValueFrom().trim()
            );

            condition.setValueTo(
                    conditionDTO.getValueTo() == null
                            ? null
                            : conditionDTO.getValueTo().trim()
            );

            conditionRepository.save(condition);
        }
    }

    // ---------------------------------------------------------
    // SAVE APPLICABILITY
    // ---------------------------------------------------------

    private void saveApplicability(
            Long taxRuleId,
            AdminTaxRuleRequestDTO requestDTO) {

        for (AdminTaxRuleRequestDTO.ApplicabilityDTO dto
                : requestDTO.getApplicability()) {

            String type = normalize(dto.getApplicabilityType());
            String code = dto.getApplicabilityCode().trim();

            TaxRuleApplicability applicability =
                    new TaxRuleApplicability();

            applicability.setTaxRuleId(taxRuleId);
            applicability.setApplicabilityType(type);
            applicability.setApplicabilityCode(code);

            switch (type) {

                case "NATIONAL":
                    break;

                case "STATE":
                    applicability.setStateCode(code);
                    break;

                case "CITY":
                    applicability.setCityId(Long.valueOf(code));
                    break;

                case "RTO":
                    applicability.setRtoCode(code);
                    break;
            }

            applicabilityRepository.save(applicability);
        }
    }

    // ---------------------------------------------------------
    // MAP ENTITY TO RESPONSE DTO
    // ---------------------------------------------------------

    private AdminTaxRuleResponseDTO mapToResponseDTO(
            TaxRule taxRule) {

        AdminTaxRuleResponseDTO response =
                new AdminTaxRuleResponseDTO();

        TaxType taxType = taxTypeRepository
                .findById(taxRule.getTaxTypeId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Tax type not found: "
                                        + taxRule.getTaxTypeId()
                        ));

        response.setTaxRuleId(taxRule.getTaxRuleId());
        response.setTaxTypeId(taxRule.getTaxTypeId());
        response.setTaxCode(taxType.getTaxCode());
        response.setTaxName(taxType.getTaxName());

        response.setRuleName(taxRule.getRuleName());

        response.setCalculationType(
                taxRule.getCalculationType()
        );

        response.setCalculationBaseId(
                taxRule.getCalculationBaseId()
        );

        if (taxRule.getCalculationBaseId() != null) {

            taxCalculationBaseRepository
                    .findById(taxRule.getCalculationBaseId())
                    .ifPresent(base -> {
                        response.setCalculationBaseCode(
                                base.getBaseCode()
                        );
                        response.setCalculationBaseName(
                                base.getBaseName()
                        );
                    });
        }

        response.setTaxValue(taxRule.getTaxValue());
        response.setTaxPercentage(taxRule.getTaxPercentage());

        response.setPriority(taxRule.getPriority());
        response.setIsFallback(taxRule.getIsFallback());
        response.setIsActive(taxRule.getIsActive());

        List<AdminTaxRuleRequestDTO.ConditionDTO> conditions =
                conditionRepository.findByTaxRuleId(
                        taxRule.getTaxRuleId()
                ).stream().map(condition -> {

                    AdminTaxRuleRequestDTO.ConditionDTO dto =
                            new AdminTaxRuleRequestDTO.ConditionDTO();

                    dto.setFieldName(condition.getFieldName());
                    dto.setOperator(condition.getOperator());
                    dto.setValueFrom(condition.getValueFrom());
                    dto.setValueTo(condition.getValueTo());

                    return dto;
                }).toList();

        List<AdminTaxRuleRequestDTO.ApplicabilityDTO> applicability =
                applicabilityRepository.findByTaxRuleId(
                        taxRule.getTaxRuleId()
                ).stream().map(item -> {

                    AdminTaxRuleRequestDTO.ApplicabilityDTO dto =
                            new AdminTaxRuleRequestDTO.ApplicabilityDTO();

                    dto.setApplicabilityType(
                            item.getApplicabilityType()
                    );

                    dto.setApplicabilityCode(
                            item.getApplicabilityCode()
                    );

                    return dto;
                }).toList();

        response.setConditions(conditions);
        response.setApplicability(applicability);

        return response;
    }

    // ---------------------------------------------------------
    // COMMON STRING NORMALIZATION
    // ---------------------------------------------------------

    private String normalize(String value) {

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim().toUpperCase(Locale.ROOT);
    }
}
