package com.dap.calculation.dto;

import java.math.BigDecimal;

public class DapCalculationResponse {

    private String vehicleId;
    private String modelName;
    private String variantName;

    private String rtoCode;
    private String stateCode;

    private String insuranceProvider;

    private BigDecimal exShowroomPrice;
    private BigDecimal roadTax;
    private BigDecimal tcs;
    private BigDecimal cess;
    private BigDecimal registrationCharges;
    private BigDecimal insurancePremium;
    private BigDecimal totalDriveAwayPrice;

    public DapCalculationResponse() {
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
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

    public String getRtoCode() {
        return rtoCode;
    }

    public void setRtoCode(String rtoCode) {
        this.rtoCode = rtoCode;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getInsuranceProvider() {
        return insuranceProvider;
    }

    public void setInsuranceProvider(String insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }

    public BigDecimal getExShowroomPrice() {
        return exShowroomPrice;
    }

    public void setExShowroomPrice(BigDecimal exShowroomPrice) {
        this.exShowroomPrice = exShowroomPrice;
    }

    public BigDecimal getRoadTax() {
        return roadTax;
    }

    public void setRoadTax(BigDecimal roadTax) {
        this.roadTax = roadTax;
    }

    public BigDecimal getTcs() {
        return tcs;
    }

    public void setTcs(BigDecimal tcs) {
        this.tcs = tcs;
    }

    public BigDecimal getCess() {
        return cess;
    }

    public void setCess(BigDecimal cess) {
        this.cess = cess;
    }

    public BigDecimal getRegistrationCharges() {
        return registrationCharges;
    }

    public void setRegistrationCharges(BigDecimal registrationCharges) {
        this.registrationCharges = registrationCharges;
    }

    public BigDecimal getInsurancePremium() {
        return insurancePremium;
    }

    public void setInsurancePremium(BigDecimal insurancePremium) {
        this.insurancePremium = insurancePremium;
    }

    public BigDecimal getTotalDriveAwayPrice() {
        return totalDriveAwayPrice;
    }

    public void setTotalDriveAwayPrice(BigDecimal totalDriveAwayPrice) {
        this.totalDriveAwayPrice = totalDriveAwayPrice;
    }
}