
package com.dap.calculation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tax_condition_fields")
public class TaxConditionField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "condition_field_id")
    private Long conditionFieldId;

    @Column(name = "field_code", nullable = false, unique = true, length = 100)
    private String fieldCode;

    @Column(name = "field_label", nullable = false, length = 100)
    private String fieldLabel;

    @Column(name = "data_type", nullable = false, length = 20)
    private String dataType;

    @Column(name = "value_source", nullable = false, length = 30)
    private String valueSource;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public TaxConditionField() {
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

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
