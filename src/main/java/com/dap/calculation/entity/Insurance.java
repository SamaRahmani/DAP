package com.dap.calculation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "insurance")
public class Insurance {

    @Id
    @Column(name = "insurance_id", length = 20)
    private String insuranceId;

    @Column(name = "provider_name", nullable = false, length = 150)
    private String providerName;

    @Column(name = "percentage", nullable = false, precision = 8, scale = 4)
    private BigDecimal percentage;

    public Insurance() {
    }

    public String getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(String insuranceId) {
        this.insuranceId = insuranceId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }
}