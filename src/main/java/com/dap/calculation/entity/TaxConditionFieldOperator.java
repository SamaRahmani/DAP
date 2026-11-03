
package com.dap.calculation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tax_condition_field_operators")
public class TaxConditionFieldOperator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "condition_field_operator_id")
    private Long conditionFieldOperatorId;

    @Column(name = "condition_field_id", nullable = false)
    private Long conditionFieldId;

    @Column(name = "operator", nullable = false, length = 30)
    private String operator;

    public TaxConditionFieldOperator() {
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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
