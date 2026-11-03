
package com.dap.calculation.admin.tax.taxcalculationbase.dto;

public class TaxCalculationBaseResponseDTO {

    private Long calculationBaseId;
    private String baseCode;
    private String baseName;
    private String description;
    private Boolean isActive;

    public TaxCalculationBaseResponseDTO() {
    }

    public Long getCalculationBaseId() {
        return calculationBaseId;
    }

    public void setCalculationBaseId(Long id) {
        this.calculationBaseId = id;
    }

    public String getBaseCode() {
        return baseCode;
    }

    public void setBaseCode(String baseCode) {
        this.baseCode = baseCode;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
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
