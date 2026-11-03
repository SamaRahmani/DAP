
package com.dap.calculation.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "tax_rule_applicability",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "tax_rule_id",
                                "applicability_type",
                                "applicability_code"
                        }
                )
        }
)
public class TaxRuleApplicability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "applicability_id")
    private Long applicabilityId;

    @Column(name = "tax_rule_id", nullable = false)
    private Long taxRuleId;

    @Column(name = "applicability_type", nullable = false, length = 20)
    private String applicabilityType;

    @Column(name = "applicability_code", nullable = false, length = 30)
    private String applicabilityCode;

    @Column(name = "state_code", length = 10)
    private String stateCode;

    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "rto_code", length = 20)
    private String rtoCode;

    public TaxRuleApplicability() {
    }

    public Long getApplicabilityId() {
        return applicabilityId;
    }

    public void setApplicabilityId(Long applicabilityId) {
        this.applicabilityId = applicabilityId;
    }

    public Long getTaxRuleId() {
        return taxRuleId;
    }

    public void setTaxRuleId(Long taxRuleId) {
        this.taxRuleId = taxRuleId;
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

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getRtoCode() {
        return rtoCode;
    }

    public void setRtoCode(String rtoCode) {
        this.rtoCode = rtoCode;
    }
}
