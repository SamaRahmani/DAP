package com.dap.calculation.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tax_rules")
public class TaxRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tax_rule_id")
    private Long taxRuleId;

    @Column(name = "tax_type_id", nullable = false)
    private Long taxTypeId;

    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;

    @Column(
            name = "tax_percentage",
            nullable = false,
            precision = 8,
            scale = 4
    )
    private BigDecimal taxPercentage;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public TaxRule() {
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
}