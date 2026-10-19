package com.dap.calculation.admin.tax.taxrule.service;

import com.dap.calculation.admin.tax.taxrule.dto.AdminTaxRuleRequestDTO;
import com.dap.calculation.admin.tax.taxrule.dto.AdminTaxRuleResponseDTO;
import com.dap.calculation.entity.TaxRule;
import com.dap.calculation.entity.TaxRuleApplicability;
import com.dap.calculation.entity.TaxRuleCondition;
import com.dap.calculation.entity.TaxType;
import com.dap.calculation.repository.TaxRuleApplicabilityRepository;
import com.dap.calculation.repository.TaxRuleConditionRepository;
import com.dap.calculation.repository.TaxRuleRepository;
import com.dap.calculation.repository.TaxTypeRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminTaxRuleService {

    private final TaxRuleRepository taxRuleRepository;
    private final TaxTypeRepository taxTypeRepository;
    private final TaxRuleConditionRepository conditionRepository;
    private final TaxRuleApplicabilityRepository applicabilityRepository;

    public AdminTaxRuleService(
            TaxRuleRepository taxRuleRepository,
            TaxTypeRepository taxTypeRepository,
            TaxRuleConditionRepository conditionRepository,
            TaxRuleApplicabilityRepository applicabilityRepository) {

        this.taxRuleRepository = taxRuleRepository;
        this.taxTypeRepository = taxTypeRepository;
        this.conditionRepository = conditionRepository;
        this.applicabilityRepository = applicabilityRepository;
    }

    // GET: Retrieve all tax rules
    @Transactional(readOnly = true)
    public List<AdminTaxRuleResponseDTO> getAllTaxRules() {
        return taxRuleRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // GET: Retrieve one tax rule with conditions and applicability
    @Transactional(readOnly = true)
    public AdminTaxRuleResponseDTO getTaxRuleById(Long taxRuleId) {

        TaxRule taxRule = findTaxRule(taxRuleId);

        return mapToResponseDTO(taxRule);
    }

    // POST: Create a tax rule with conditions and applicability
    @Transactional
    public AdminTaxRuleResponseDTO createTaxRule(
            AdminTaxRuleRequestDTO requestDTO) {

        validateRequest(requestDTO);
        validateTaxType(requestDTO.getTaxTypeId());

        TaxRule taxRule = new TaxRule();

        taxRule.setTaxTypeId(requestDTO.getTaxTypeId());
        taxRule.setRuleName(requestDTO.getRuleName().trim());
        taxRule.setTaxPercentage(requestDTO.getTaxPercentage());
        taxRule.setIsActive(
                requestDTO.getIsActive() == null
                        || requestDTO.getIsActive()
        );

        TaxRule savedRule = taxRuleRepository.save(taxRule);

        saveConditions(savedRule.getTaxRuleId(), requestDTO);
        saveApplicability(savedRule.getTaxRuleId(), requestDTO);

        return mapToResponseDTO(savedRule);
    }

    // PUT: Update a tax rule and replace its configuration
    @Transactional
    public AdminTaxRuleResponseDTO updateTaxRule(
            Long taxRuleId,
            AdminTaxRuleRequestDTO requestDTO) {

        validateRequest(requestDTO);
        validateTaxType(requestDTO.getTaxTypeId());

        TaxRule existingRule = findTaxRule(taxRuleId);

        existingRule.setTaxTypeId(requestDTO.getTaxTypeId());
        existingRule.setRuleName(requestDTO.getRuleName().trim());
        existingRule.setTaxPercentage(requestDTO.getTaxPercentage());

        if (requestDTO.getIsActive() != null) {
            existingRule.setIsActive(requestDTO.getIsActive());
        }

        TaxRule updatedRule = taxRuleRepository.save(existingRule);

        // Replace the existing conditions and applicability.
        conditionRepository.deleteByTaxRuleId(taxRuleId);
        applicabilityRepository.deleteByTaxRuleId(taxRuleId);

        saveConditions(taxRuleId, requestDTO);
        saveApplicability(taxRuleId, requestDTO);

        return mapToResponseDTO(updatedRule);
    }

    // DELETE: Delete a tax rule and its dependent configuration
    @Transactional
    public void deleteTaxRule(Long taxRuleId) {

        TaxRule existingRule = findTaxRule(taxRuleId);

        conditionRepository.deleteByTaxRuleId(taxRuleId);
        applicabilityRepository.deleteByTaxRuleId(taxRuleId);

        taxRuleRepository.delete(existingRule);
    }

    private TaxRule findTaxRule(Long taxRuleId) {
        return taxRuleRepository.findById(taxRuleId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Tax rule not found with ID: " + taxRuleId
                        ));
    }

    private void validateTaxType(Long taxTypeId) {

        if (taxTypeId == null
                || !taxTypeRepository.existsById(taxTypeId)) {
            throw new IllegalArgumentException(
                    "Valid taxTypeId is required."
            );
        }
    }

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

        BigDecimal percentage = requestDTO.getTaxPercentage();

        if (percentage == null
                || percentage.compareTo(BigDecimal.ZERO) < 0
                || percentage.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException(
                    "Tax percentage must be between 0 and 100."
            );
        }

        if (requestDTO.getConditions() == null
                || requestDTO.getConditions().isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one condition is required."
            );
        }

        if (requestDTO.getApplicability() == null
                || requestDTO.getApplicability().isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one applicability record is required."
            );
        }

        requestDTO.getConditions().forEach(this::validateCondition);
        requestDTO.getApplicability().forEach(this::validateApplicability);
    }

    private void validateCondition(
            AdminTaxRuleRequestDTO.ConditionDTO condition) {

        if (condition == null
                || condition.getFieldName() == null
                || condition.getFieldName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Condition field name is required."
            );
        }

        if (condition.getOperator() == null) {
            throw new IllegalArgumentException(
                    "Condition operator is required."
            );
        }

        String operator = condition.getOperator()
                .trim()
                .toUpperCase();

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

            try {
                if (new BigDecimal(valueFrom).compareTo(
                        new BigDecimal(valueTo)) > 0) {
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
                    "valueTo must be null unless the operator is BETWEEN."
            );
        }
    }

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

        String type = applicability.getApplicabilityType()
                .trim()
                .toUpperCase();

        if (!List.of("NATIONAL", "STATE", "CITY", "RTO")
                .contains(type)) {
            throw new IllegalArgumentException(
                    "Applicability type must be NATIONAL, STATE, CITY, or RTO."
            );
        }

        if ("NATIONAL".equals(type)
                && !List.of("ALL", "INDIA").contains(
                applicability.getApplicabilityCode()
                        .trim().toUpperCase())) {
            throw new IllegalArgumentException(
                    "National applicability code must be ALL or INDIA."
            );
        }
    }

    private void saveConditions(
            Long taxRuleId,
            AdminTaxRuleRequestDTO requestDTO) {

        for (AdminTaxRuleRequestDTO.ConditionDTO conditionDTO
                : requestDTO.getConditions()) {

            TaxRuleCondition condition = new TaxRuleCondition();

            condition.setTaxRuleId(taxRuleId);
            condition.setFieldName(conditionDTO.getFieldName().trim());
            condition.setOperator(
                    conditionDTO.getOperator().trim().toUpperCase()
            );
            condition.setValueFrom(conditionDTO.getValueFrom().trim());
            condition.setValueTo(
                    conditionDTO.getValueTo() == null
                            ? null
                            : conditionDTO.getValueTo().trim()
            );

            conditionRepository.save(condition);
        }
    }

    private void saveApplicability(
            Long taxRuleId,
            AdminTaxRuleRequestDTO requestDTO) {

        for (AdminTaxRuleRequestDTO.ApplicabilityDTO applicabilityDTO
                : requestDTO.getApplicability()) {

            TaxRuleApplicability applicability =
                    new TaxRuleApplicability();

            applicability.setTaxRuleId(taxRuleId);
            applicability.setApplicabilityType(
                    applicabilityDTO.getApplicabilityType()
                            .trim().toUpperCase()
            );
            applicability.setApplicabilityCode(
                    applicabilityDTO.getApplicabilityCode().trim()
            );

            applicabilityRepository.save(applicability);
        }
    }

    private AdminTaxRuleResponseDTO mapToResponseDTO(TaxRule taxRule) {

        AdminTaxRuleResponseDTO response =
                new AdminTaxRuleResponseDTO();

        TaxType taxType = taxTypeRepository.findById(
                taxRule.getTaxTypeId()
        ).orElseThrow(() ->
                new RuntimeException(
                        "Tax type not found: " + taxRule.getTaxTypeId()
                ));

        response.setTaxRuleId(taxRule.getTaxRuleId());
        response.setTaxTypeId(taxRule.getTaxTypeId());
        response.setTaxCode(taxType.getTaxCode());
        response.setTaxName(taxType.getTaxName());
        response.setRuleName(taxRule.getRuleName());
        response.setTaxPercentage(taxRule.getTaxPercentage());
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

                    dto.setApplicabilityType(item.getApplicabilityType());
                    dto.setApplicabilityCode(item.getApplicabilityCode());

                    return dto;
                }).toList();

        response.setConditions(conditions);
        response.setApplicability(applicability);

        return response;
    }
}