
package com.dap.calculation.admin.tax.taxconditionfield.dto;

public class TaxConditionFieldResponseDTO {

    private Long conditionFieldId;
    private String fieldCode;
    private String fieldLabel;
    private String dataType;
    private String valueSource;
    private String description;
    private Boolean isActive;

    public TaxConditionFieldResponseDTO() {
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

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getValueSource() {
        return valueSource;
    }

    public void setValueSource(String valueSource) {
        this.valueSource = valueSource;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
