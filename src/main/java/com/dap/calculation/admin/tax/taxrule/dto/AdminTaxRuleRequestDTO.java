
package com.dap.calculation.admin.tax.taxrule.dto;

import java.math.BigDecimal;
import java.util.List;

public class AdminTaxRuleRequestDTO {

    private Long taxTypeId;
    private String ruleName;

    private String calculationType;
    private Long calculationBaseId;
    private BigDecimal taxValue;

    // Backward compatibility with the old API.
    private BigDecimal taxPercentage;

    private Integer priority;
    private Boolean isFallback;
    private Boolean isActive;

    private List<ConditionDTO> conditions;
    private List<ApplicabilityDTO> applicability;

    public AdminTaxRuleRequestDTO() {
    }

    public Long getTaxTypeId() { return taxTypeId; }
    public void setTaxTypeId(Long taxTypeId) { this.taxTypeId = taxTypeId; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public String getCalculationType() { return calculationType; }
    public void setCalculationType(String calculationType) { this.calculationType = calculationType; }

    public Long getCalculationBaseId() { return calculationBaseId; }
    public void setCalculationBaseId(Long calculationBaseId) { this.calculationBaseId = calculationBaseId; }

    public BigDecimal getTaxValue() { return taxValue; }
    public void setTaxValue(BigDecimal taxValue) { this.taxValue = taxValue; }

    public BigDecimal getTaxPercentage() { return taxPercentage; }
    public void setTaxPercentage(BigDecimal taxPercentage) { this.taxPercentage = taxPercentage; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public Boolean getIsFallback() { return isFallback; }
    public void setIsFallback(Boolean fallback) { isFallback = fallback; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }

    public List<ConditionDTO> getConditions() { return conditions; }
    public void setConditions(List<ConditionDTO> conditions) { this.conditions = conditions; }

    public List<ApplicabilityDTO> getApplicability() { return applicability; }
    public void setApplicability(List<ApplicabilityDTO> applicability) { this.applicability = applicability; }

    public static class ConditionDTO {

        private String fieldName;
        private String operator;
        private String valueFrom;
        private String valueTo;

        public ConditionDTO() {
        }

        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }

        public String getOperator() { return operator; }
        public void setOperator(String operator) { this.operator = operator; }

        public String getValueFrom() { return valueFrom; }
        public void setValueFrom(String valueFrom) { this.valueFrom = valueFrom; }

        public String getValueTo() { return valueTo; }
        public void setValueTo(String valueTo) { this.valueTo = valueTo; }
    }

    public static class ApplicabilityDTO {

        private String applicabilityType;
        private String applicabilityCode;

        public ApplicabilityDTO() {
        }

        public String getApplicabilityType() { return applicabilityType; }
        public void setApplicabilityType(String applicabilityType) { this.applicabilityType = applicabilityType; }

        public String getApplicabilityCode() { return applicabilityCode; }
        public void setApplicabilityCode(String applicabilityCode) { this.applicabilityCode = applicabilityCode; }
    }
}
