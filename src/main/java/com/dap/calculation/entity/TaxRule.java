
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

    // Retained for compatibility with the existing database column.
    @Column(name = "tax_percentage", nullable = false, precision = 8, scale = 4)
    private BigDecimal taxPercentage;

    @Column(name = "calculation_type", nullable = false, length = 20)
    private String calculationType = "PERCENTAGE";

    @Column(name = "calculation_base_id")
    private Long calculationBaseId;

    @Column(name = "tax_value", nullable = false, precision = 15, scale = 4)
    private BigDecimal taxValue;

    @Column(name = "priority", nullable = false)
    private Integer priority = 100;

    @Column(name = "is_fallback", nullable = false)
    private Boolean isFallback = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public TaxRule() {
    }

    public Long getTaxRuleId() { return taxRuleId; }
    public void setTaxRuleId(Long taxRuleId) { this.taxRuleId = taxRuleId; }

    public Long getTaxTypeId() { return taxTypeId; }
    public void setTaxTypeId(Long taxTypeId) { this.taxTypeId = taxTypeId; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public BigDecimal getTaxPercentage() { return taxPercentage; }
    public void setTaxPercentage(BigDecimal taxPercentage) { this.taxPercentage = taxPercentage; }

    public String getCalculationType() { return calculationType; }
    public void setCalculationType(String calculationType) { this.calculationType = calculationType; }

    public Long getCalculationBaseId() { return calculationBaseId; }
    public void setCalculationBaseId(Long calculationBaseId) { this.calculationBaseId = calculationBaseId; }

    public BigDecimal getTaxValue() { return taxValue; }
    public void setTaxValue(BigDecimal taxValue) { this.taxValue = taxValue; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public Boolean getIsFallback() { return isFallback; }
    public void setIsFallback(Boolean fallback) { isFallback = fallback; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
}
