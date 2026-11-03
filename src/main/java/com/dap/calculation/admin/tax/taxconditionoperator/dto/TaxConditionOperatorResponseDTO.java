
package com.dap.calculation.admin.tax.taxconditionoperator.dto;

public class TaxConditionOperatorResponseDTO {

    private Long conditionFieldOperatorId;
    private Long conditionFieldId;
    private String fieldCode;
    private String fieldLabel;
    private String operator;

    public TaxConditionOperatorResponseDTO() {
    }

    public Long getConditionFieldOperatorId() {
        return conditionFieldOperatorId;
    }

    public void setConditionFieldOperatorId(Long id) {
        this.conditionFieldOperatorId = id;
    }

    public Long getConditionFieldId() {
        return conditionFieldId;
    }

    public void setConditionFieldId(Long conditionFieldId) {
        this.conditionFieldId = conditionFieldId;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
