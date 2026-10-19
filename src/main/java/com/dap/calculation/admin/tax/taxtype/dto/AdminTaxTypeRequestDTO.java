package com.dap.calculation.admin.tax.taxtype.dto;

public class AdminTaxTypeRequestDTO {

    private String taxCode;
    private String taxName;
    private String description;

    public AdminTaxTypeRequestDTO() {
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