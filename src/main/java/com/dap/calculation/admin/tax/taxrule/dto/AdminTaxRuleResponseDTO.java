
package com.dap.calculation.admin.tax.taxrule.dto;

import java.math.BigDecimal;
import java.util.List;

public class AdminTaxRuleResponseDTO {

    private Long taxRuleId;
    private Long taxTypeId;
    private String taxCode;
    private String taxName;
    private String ruleName;

    private String calculationType;
    private Long calculationBaseId;
    private String calculationBaseCode;
    private String calculationBaseName;
    private BigDecimal taxValue;

    // Backward compatibility.
    private BigDecimal taxPercentage;

    private Integer priority;
    private Boolean isFallback;
    private Boolean isActive;

    private List<AdminTaxRuleRequestDTO.ConditionDTO> conditions;
    private List<AdminTaxRuleRequestDTO.ApplicabilityDTO> applicability;

    public AdminTaxRuleResponseDTO() {
    }

    public Long getTaxRuleId() { return taxRuleId; }
    public void setTaxRuleId(Long id) { this.taxRuleId = id; }

    public Long getTaxTypeId() { return taxTypeId; }
    public void setTaxTypeId(Long id) { this.taxTypeId = id; }

    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }

    public String getTaxName() { return taxName; }
    public void setTaxName(String taxName) { this.taxName = taxName; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public String getCalculationType() { return calculationType; }
    public void setCalculationType(String type) { this.calculationType = type; }

    public Long getCalculationBaseId() { return calculationBaseId; }
    public void setCalculationBaseId(Long id) { this.calculationBaseId = id; }

    public String getCalculationBaseCode() { return calculationBaseCode; }
    public void setCalculationBaseCode(String code) { this.calculationBaseCode = code; }

    public String getCalculationBaseName() { return calculationBaseName; }
    public void setCalculationBaseName(String name) { this.calculationBaseName = name; }

    public BigDecimal getTaxValue() { return taxValue; }
    public void setTaxValue(BigDecimal value) { this.taxValue = value; }

    public BigDecimal getTaxPercentage() { return taxPercentage; }
    public void setTaxPercentage(BigDecimal percentage) { this.taxPercentage = percentage; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public Boolean getIsFallback() { return isFallback; }
    public void setIsFallback(Boolean fallback) { isFallback = fallback; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }

    public List<AdminTaxRuleRequestDTO.ConditionDTO> getConditions() { return conditions; }
    public void setConditions(List<AdminTaxRuleRequestDTO.ConditionDTO> conditions) { this.conditions = conditions; }

    public List<AdminTaxRuleRequestDTO.ApplicabilityDTO> getApplicability() { return applicability; }
    public void setApplicability(List<AdminTaxRuleRequestDTO.ApplicabilityDTO> applicability) { this.applicability = applicability; }
}
