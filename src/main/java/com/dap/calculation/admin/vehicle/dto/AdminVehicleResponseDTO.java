package com.dap.calculation.admin.vehicle.dto;

import java.math.BigDecimal;

public class AdminVehicleResponseDTO {

    private String vehicleId;
    private String modelName;
    private String variantName;
    private String fuelType;
    private String transmissionType;
    private BigDecimal exShowroomPrice;
    private BigDecimal lengthMm;
    private BigDecimal groundClearanceMm;

    public AdminVehicleResponseDTO() {
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

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(String transmissionType) {
        this.transmissionType = transmissionType;
    }

    public BigDecimal getExShowroomPrice() {
        return exShowroomPrice;
    }

    public void setExShowroomPrice(BigDecimal exShowroomPrice) {
        this.exShowroomPrice = exShowroomPrice;
    }

    public BigDecimal getLengthMm() {
        return lengthMm;
    }

    public void setLengthMm(BigDecimal lengthMm) {
        this.lengthMm = lengthMm;
    }

    public BigDecimal getGroundClearanceMm() {
        return groundClearanceMm;
    }

    public void setGroundClearanceMm(BigDecimal groundClearanceMm) {
        this.groundClearanceMm = groundClearanceMm;
    }
}