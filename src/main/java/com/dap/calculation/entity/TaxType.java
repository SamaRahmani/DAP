package com.dap.calculation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tax_types")
public class TaxType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tax_type_id")
    private Long taxTypeId;

    @Column(name = "tax_code", nullable = false, unique = true, length = 30)
    private String taxCode;

    @Column(name = "tax_name", nullable = false, length = 100)
    private String taxName;

    @Column(name = "description", length = 255)
    private String description;

    public TaxType() {
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