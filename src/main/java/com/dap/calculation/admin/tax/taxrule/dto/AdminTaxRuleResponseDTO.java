package com.dap.calculation.admin.tax.taxrule.dto;

import java.math.BigDecimal;
import java.util.List;

public class AdminTaxRuleResponseDTO {

    private Long taxRuleId;
    private Long taxTypeId;
    private String taxCode;
    private String taxName;
    private String ruleName;
    private BigDecimal taxPercentage;
    private Boolean isActive;

    private List<AdminTaxRuleRequestDTO.ConditionDTO> conditions;
    private List<AdminTaxRuleRequestDTO.ApplicabilityDTO> applicability;

    public AdminTaxRuleResponseDTO() {
    }

    public Long getTaxRuleId() {
        return taxRuleId;
    }

    public void setTaxRuleId(Long taxRuleId) {
        this.taxRuleId = taxRuleId;
    }

    public Long getTaxTypeId() {
        return taxTypeId;
    }

    public void setTaxTypeId(Long taxTypeId) {
        this.taxTypeId = taxTypeId;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public BigDecimal getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(BigDecimal taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public List<AdminTaxRuleRequestDTO.ConditionDTO> getConditions() {
        return conditions;
    }

    public void setConditions(
            List<AdminTaxRuleRequestDTO.ConditionDTO> conditions) {
        this.conditions = conditions;
    }

    public List<AdminTaxRuleRequestDTO.ApplicabilityDTO> getApplicability() {
        return applicability;
    }

    public void setApplicability(
            List<AdminTaxRuleRequestDTO.ApplicabilityDTO> applicability) {
        this.applicability = applicability;
    }
}