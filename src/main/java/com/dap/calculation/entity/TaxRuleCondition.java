package com.dap.calculation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tax_rule_conditions")
public class TaxRuleCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tax_rule_condition_id")
    private Long taxRuleConditionId;

    @Column(name = "tax_rule_id", nullable = false)
    private Long taxRuleId;

    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName;

    @Column(name = "operator", nullable = false, length = 30)
    private String operator;

    @Column(name = "value_from", length = 255)
    private String valueFrom;

    @Column(name = "value_to", length = 255)
    private String valueTo;

    public TaxRuleCondition() {
    }

    public Long getTaxRuleConditionId() {
        return taxRuleConditionId;
    }

    public void setTaxRuleConditionId(Long taxRuleConditionId) {
        this.taxRuleConditionId = taxRuleConditionId;
    }

    public Long getTaxRuleId() {
        return taxRuleId;
    }

    public void setTaxRuleId(Long taxRuleId) {
        this.taxRuleId = taxRuleId;
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