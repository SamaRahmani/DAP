package com.dap.calculation.admin.tax.taxrule.dto;

import java.math.BigDecimal;
import java.util.List;

public class AdminTaxRuleRequestDTO {

    private Long taxTypeId;
    private String ruleName;
    private BigDecimal taxPercentage;
    private Boolean isActive;

    private List<ConditionDTO> conditions;
    private List<ApplicabilityDTO> applicability;

    public AdminTaxRuleRequestDTO() {
    }

    public Long getTaxTypeId() {
        return taxTypeId;
    }

    public void setTaxTypeId(Long taxTypeId) {
        this.taxTypeId = taxTypeId;
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

    public List<ConditionDTO> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionDTO> conditions) {
        this.conditions = conditions;
    }

    public List<ApplicabilityDTO> getApplicability() {
        return applicability;
    }

    public void setApplicability(List<ApplicabilityDTO> applicability) {
        this.applicability = applicability;
    }

    public static class ConditionDTO {

        private String fieldName;
        private String operator;
        private String valueFrom;
        private String valueTo;

        public ConditionDTO() {
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getValueFrom() {
            return valueFrom;
        }

        public void setValueFrom(String valueFrom) {
            this.valueFrom = valueFrom;
        }

        public String getValueTo() {
            return valueTo;
        }

        public void setValueTo(String valueTo) {
            this.valueTo = valueTo;
        }
    }

    public static class ApplicabilityDTO {

        private String applicabilityType;
        private String applicabilityCode;

        public ApplicabilityDTO() {
        }

        public String getApplicabilityType() {
            return applicabilityType;
        }

        public void setApplicabilityType(String applicabilityType) {
            this.applicabilityType = applicabilityType;
        }

        public String getApplicabilityCode() {
            return applicabilityCode;
        }

        public void setApplicabilityCode(String applicabilityCode) {
            this.applicabilityCode = applicabilityCode;
        }
    }
}