package com.dap.calculation.admin.insurance.dto;

import java.math.BigDecimal;

public class AdminInsuranceResponseDTO {

    private String insuranceId;
    private String providerName;
    private BigDecimal percentage;

    public AdminInsuranceResponseDTO() {
    }

    public AdminInsuranceResponseDTO(
            String insuranceId,
            String providerName,
            BigDecimal percentage) {

        this.insuranceId = insuranceId;
        this.providerName = providerName;
        this.percentage = percentage;
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