package com.dap.calculation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;

@Entity
@Table(
        name = "tax_rules",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"tax_type_id", "state_code"}
                )
        }
)
public class TaxRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tax_rule_id")
    private Long taxRuleId;

    @Column(name = "tax_type_id", nullable = false, length = 20)
    private String taxTypeId;

    @Column(name = "state_code", nullable = false, length = 10)
    private String stateCode;

    @Column(name = "tax_percentage", nullable = false, precision = 8, scale = 4)
    private BigDecimal taxPercentage;

    @Column(name = "calculation_basis", nullable = false, length = 50)
    private String calculationBasis;

    public TaxRule() {
    }

    public Long getTaxRuleId() {
        return taxRuleId;
    }

    public void setTaxRuleId(Long taxRuleId) {
        this.taxRuleId = taxRuleId;
    }

    public String getTaxTypeId() {
        return taxTypeId;
    }

    public void setTaxTypeId(String taxTypeId) {
        this.taxTypeId = taxTypeId;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public BigDecimal getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(BigDecimal taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public String getCalculationBasis() {
        return calculationBasis;
    }

    public void setCalculationBasis(String calculationBasis) {
        this.calculationBasis = calculationBasis;
    }
}