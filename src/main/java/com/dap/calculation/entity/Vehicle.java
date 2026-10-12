package com.dap.calculation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @Column(name = "vehicle_id", length = 30)
    private String vehicleId;
    @Column(name = "model_name", nullable = false, length = 100)
    private String modelName;
    @Column(name = "variant_name", nullable = false, length = 100)
    private String variantName;
    @Column(name = "fuel_type", nullable = false, length = 30)
    private String fuelType;
    @Column(name = "transmission_type", nullable = false, length = 30)
    private String transmissionType;
    @Column(name = "ex_showroom_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal exShowroomPrice;
    @Column(name = "length_mm", nullable = false, precision = 10, scale = 2)
    private BigDecimal lengthMm;
    @Column(name = "ground_clearance_mm", nullable = false, precision = 10, scale = 2)
    private BigDecimal groundClearanceMm;
    public Vehicle() {
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