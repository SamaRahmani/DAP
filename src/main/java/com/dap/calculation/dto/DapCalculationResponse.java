
package com.dap.calculation.dto;

import java.math.BigDecimal;

public class DapCalculationResponse {

    private String modelName;
    private String variantName;

    private String stateName;
    private String cityName;
    private String rtoCode;

    private String insuranceProvider;
    private BigDecimal insurancePercentage;

    private BigDecimal exShowroomPrice;
    private BigDecimal insurancePremium;

    private BigDecimal nationalCharges;
    private BigDecimal stateCharges;
    private BigDecimal cityCharges;

    private BigDecimal registrationCharges;
    private BigDecimal totalDriveAwayPrice;

    public DapCalculationResponse() {
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getRtoCode() {
        return rtoCode;
    }

    public void setRtoCode(String rtoCode) {
        this.rtoCode = rtoCode;
    }

    public String getInsuranceProvider() {
        return insuranceProvider;
    }

    public void setInsuranceProvider(String insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }

    public BigDecimal getInsurancePercentage() {
        return insurancePercentage;
    }

    public void setInsurancePercentage(BigDecimal insurancePercentage) {
        this.insurancePercentage = insurancePercentage;
    }

    public BigDecimal getExShowroomPrice() {
        return exShowroomPrice;
    }

    public void setExShowroomPrice(BigDecimal exShowroomPrice) {
        this.exShowroomPrice = exShowroomPrice;
    }

    public BigDecimal getInsurancePremium() {
        return insurancePremium;
    }

    public void setInsurancePremium(BigDecimal insurancePremium) {
        this.insurancePremium = insurancePremium;
    }

    public BigDecimal getNationalCharges() {
        return nationalCharges;
    }

    public void setNationalCharges(BigDecimal nationalCharges) {
        this.nationalCharges = nationalCharges;
    }

    public BigDecimal getStateCharges() {
        return stateCharges;
    }

    public void setStateCharges(BigDecimal stateCharges) {
        this.stateCharges = stateCharges;
    }

    public BigDecimal getCityCharges() {
        return cityCharges;
    }

    public void setCityCharges(BigDecimal cityCharges) {
        this.cityCharges = cityCharges;
    }

    public BigDecimal getRegistrationCharges() {
        return registrationCharges;
    }

    public void setRegistrationCharges(BigDecimal registrationCharges) {
        this.registrationCharges = registrationCharges;
    }

    public BigDecimal getTotalDriveAwayPrice() {
        return totalDriveAwayPrice;
    }

    public void setTotalDriveAwayPrice(BigDecimal totalDriveAwayPrice) {
        this.totalDriveAwayPrice = totalDriveAwayPrice;
    }
}
