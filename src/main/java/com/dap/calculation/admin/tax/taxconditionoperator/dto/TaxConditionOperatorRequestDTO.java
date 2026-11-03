
package com.dap.calculation.admin.tax.taxconditionoperator.dto;

public class TaxConditionOperatorRequestDTO {

    private Long conditionFieldId;
    private String operator;

    public TaxConditionOperatorRequestDTO() {
    }

    public Long getConditionFieldId() {
        return conditionFieldId;
    }

    public void setConditionFieldId(Long conditionFieldId) {
        this.conditionFieldId = conditionFieldId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
