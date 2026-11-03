
package com.dap.calculation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tax_calculation_bases")
public class TaxCalculationBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calculation_base_id")
    private Long calculationBaseId;

    @Column(name = "base_code", nullable = false, unique = true, length = 50)
    private String baseCode;

    @Column(name = "base_name", nullable = false, length = 100)
    private String baseName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public TaxCalculationBase() {
    }

    public Long getCalculationBaseId() { return calculationBaseId; }
    public void setCalculationBaseId(Long id) { this.calculationBaseId = id; }

    public String getBaseCode() { return baseCode; }
    public void setBaseCode(String baseCode) { this.baseCode = baseCode; }

    public String getBaseName() { return baseName; }
    public void setBaseName(String baseName) { this.baseName = baseName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
}
