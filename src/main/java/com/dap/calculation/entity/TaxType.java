package com.dap.calculation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tax_type")
public class TaxType {

    @Id
    @Column(name = "tax_type_id", length = 20)
    private String taxTypeId;

    @Column(name = "tax_name", nullable = false, unique = true, length = 50)
    private String taxName;

    public TaxType() {
    }

    public String getTaxTypeId() {
        return taxTypeId;
    }

    public void setTaxTypeId(String taxTypeId) {
        this.taxTypeId = taxTypeId;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }
}