package com.dap.calculation.admin.tax.taxtype.dto;

public class AdminTaxTypeResponseDTO {

    private Long taxTypeId;
    private String taxCode;
    private String taxName;
    private String description;

    public AdminTaxTypeResponseDTO() {
    }

    public Long getTaxTypeId() {
        return taxTypeId;
    }

    public void setTaxTypeId(Long taxTypeId) {
        this.taxTypeId = taxTypeId;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}